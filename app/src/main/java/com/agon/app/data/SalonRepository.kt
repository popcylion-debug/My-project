package com.agon.app.data

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest
import java.util.UUID

class SalonRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dir = File(context.filesDir, "salon").apply { mkdirs() }

    private val _me = MutableStateFlow<UserProfile?>(null)
    val me: StateFlow<UserProfile?> = _me.asStateFlow()

    private val _people = MutableStateFlow<List<UserProfile>>(emptyList())
    val people: StateFlow<List<UserProfile>> = _people.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _statuses = MutableStateFlow<List<StatusUpdate>>(emptyList())
    val statuses: StateFlow<List<StatusUpdate>> = _statuses.asStateFlow()

    private val _rooms = MutableStateFlow<List<VoiceRoom>>(emptyList())
    val rooms: StateFlow<List<VoiceRoom>> = _rooms.asStateFlow()

    private val _clips = MutableStateFlow<List<VoiceClip>>(emptyList())
    val clips: StateFlow<List<VoiceClip>> = _clips.asStateFlow()

    private val _ai = MutableStateFlow<List<AiTurn>>(emptyList())
    val ai: StateFlow<List<AiTurn>> = _ai.asStateFlow()

    val bus = MqttBus { env -> scope.launch { ingest(env) } }
    val link: StateFlow<MqttBus.Link> = bus.link

    init {
        loadAll()
        pruneStatuses()
    }

    fun startNetwork() {
        val id = _me.value?.id ?: "guest"
        bus.start(id)
        scope.launch {
            while (isActive) {
                announcePresence()
                delay(20_000)
            }
        }
        scope.launch {
            while (isActive) {
                pruneStatuses()
                delay(60_000)
            }
        }
    }

    fun current(): UserProfile? = _me.value

    suspend fun register(profile: UserProfile, rawPassword: String): Result<UserProfile> =
        withContext(Dispatchers.IO) {
            val handle = profile.handle.trim().lowercase()
            if (handle.length < 3) return@withContext Result.failure(IllegalArgumentException("Handle too short"))
            if (rawPassword.length < 6) return@withContext Result.failure(IllegalArgumentException("short"))
            val locals = loadUsers()
            if (locals.any { it.handle.equals(handle, true) }) {
                return@withContext Result.failure(IllegalArgumentException("taken"))
            }
            val created = profile.copy(
                id = UUID.randomUUID().toString(),
                handle = handle,
                passwordHash = hash(rawPassword),
                createdAt = System.currentTimeMillis(),
                lastSeen = System.currentTimeMillis(),
                online = true,
            )
            saveUsers(locals + created)
            _me.value = created
            persistMe()
            announceProfile(created)
            Result.success(created)
        }

    suspend fun login(handle: String, rawPassword: String): Result<UserProfile> =
        withContext(Dispatchers.IO) {
            val user = loadUsers().firstOrNull { it.handle.equals(handle.trim(), true) }
                ?: return@withContext Result.failure(IllegalArgumentException("bad"))
            if (user.passwordHash != hash(rawPassword)) {
                return@withContext Result.failure(IllegalArgumentException("bad"))
            }
            val live = user.copy(lastSeen = System.currentTimeMillis(), online = true)
            upsertLocalUser(live)
            _me.value = live
            persistMe()
            announceProfile(live)
            Result.success(live)
        }

    suspend fun restoreSession(userId: String) {
        withContext(Dispatchers.IO) {
            val user = loadUsers().firstOrNull { it.id == userId } ?: return@withContext
            _me.value = user.copy(online = true, lastSeen = System.currentTimeMillis())
            persistMe()
            announceProfile(_me.value!!)
        }
    }

    suspend fun updateProfile(transform: (UserProfile) -> UserProfile) {
        val current = _me.value ?: return
        val next = transform(current).copy(lastSeen = System.currentTimeMillis())
        _me.value = next
        persistMe()
        upsertLocalUser(next)
        announceProfile(next)
    }

    suspend fun signOut() {
        _me.value?.let { announcePresence(it.copy(online = false)) }
        _me.value = null
        File(dir, "session.json").delete()
    }

    suspend fun sendText(to: UserProfile, text: String) {
        val me = _me.value ?: return
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationIdFor(me.id, to.id),
            senderId = me.id,
            senderName = me.displayName,
            recipientId = to.id,
            text = text.trim(),
        )
        appendMessage(msg)
        publish("chat", json.encodeToString(ChatMessage.serializer(), msg))
    }

    suspend fun sendMedia(to: UserProfile, uri: Uri, mediaType: String, caption: String = "") {
        val me = _me.value ?: return
        val ext = if (mediaType.startsWith("video")) "mp4" else "jpg"
        val url = ImageHost.upload(context, uri, "snwy_${System.currentTimeMillis()}.$ext")
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationIdFor(me.id, to.id),
            senderId = me.id,
            senderName = me.displayName,
            recipientId = to.id,
            text = caption,
            mediaUrl = url,
            mediaType = mediaType,
        )
        appendMessage(msg)
        publish("chat", json.encodeToString(ChatMessage.serializer(), msg))
    }

    suspend fun sendVoiceNote(to: UserProfile, file: File, durationMs: Long) {
        val me = _me.value ?: return
        val bytes = file.readBytes()
        val url = ImageHost.uploadBytes(bytes, "voice_${System.currentTimeMillis()}.m4a", "audio/mp4")
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationIdFor(me.id, to.id),
            senderId = me.id,
            senderName = me.displayName,
            recipientId = to.id,
            mediaUrl = url,
            mediaType = "audio",
            durationMs = durationMs,
        )
        appendMessage(msg)
        publish("chat", json.encodeToString(ChatMessage.serializer(), msg))
    }

    suspend fun postStatus(text: String, uri: Uri?, mediaType: String) {
        val me = _me.value ?: return
        val url = if (uri != null) {
            val ext = if (mediaType.startsWith("video")) "mp4" else "jpg"
            ImageHost.upload(context, uri, "status_${System.currentTimeMillis()}.$ext")
        } else ""
        val status = StatusUpdate(
            id = UUID.randomUUID().toString(),
            userId = me.id,
            userName = me.displayName,
            userPhoto = me.photoUrl,
            text = text.trim(),
            mediaUrl = url,
            mediaType = if (uri == null) "" else mediaType,
        )
        _statuses.update { listOf(status) + it }
        persistStatuses()
        publish("status", json.encodeToString(StatusUpdate.serializer(), status))
    }

    suspend fun createRoom(title: String, topic: String) {
        val me = _me.value ?: return
        val room = VoiceRoom(
            id = UUID.randomUUID().toString(),
            title = title.ifBlank { "${me.displayName}'s room" },
            hostId = me.id,
            hostName = me.displayName,
            topic = topic,
        )
        _rooms.update { listOf(room) + it }
        persistRooms()
        publish("room", json.encodeToString(VoiceRoom.serializer(), room))
    }

    suspend fun sendRoomClip(room: VoiceRoom, file: File, durationMs: Long) {
        val me = _me.value ?: return
        val url = ImageHost.uploadBytes(file.readBytes(), "room_${System.currentTimeMillis()}.m4a", "audio/mp4")
        val clip = VoiceClip(
            id = UUID.randomUUID().toString(),
            roomId = room.id,
            senderId = me.id,
            senderName = me.displayName,
            mediaUrl = url,
            durationMs = durationMs,
        )
        _clips.update { it + clip }
        persistClips()
        publish("clip", json.encodeToString(VoiceClip.serializer(), clip))
    }

    fun askAi(question: String) {
        val q = question.trim()
        if (q.isBlank()) return
        val userTurn = AiTurn(UUID.randomUUID().toString(), true, q)
        val answer = Knowledge.answer(q)
        val botTurn = AiTurn(UUID.randomUUID().toString(), false, answer)
        _ai.update { it + userTurn + botTurn }
        persistAi()
    }

    fun conversations(): List<ConversationPreview> {
        val me = _me.value ?: return emptyList()
        return _messages.value
            .groupBy { it.conversationId }
            .mapNotNull { (cid, msgs) ->
                val last = msgs.maxByOrNull { it.createdAt } ?: return@mapNotNull null
                val peerId = if (last.senderId == me.id) last.recipientId else last.senderId
                if (peerId.isBlank()) return@mapNotNull null
                val peer = _people.value.firstOrNull { it.id == peerId }
                    ?: UserProfile(id = peerId, handle = peerId.take(8), displayName = last.senderName)
                ConversationPreview(
                    conversationId = cid,
                    peerId = peerId,
                    peerName = if (peerId == me.id) me.displayName else peer.displayName,
                    peerPhoto = peer.photoUrl,
                    lastText = last.text.ifBlank {
                        when (last.mediaType) {
                            "image" -> "Photo"
                            "video" -> "Video"
                            "audio" -> "Voice note"
                            else -> "Message"
                        }
                    },
                    lastAt = last.createdAt,
                    unread = 0,
                    online = peer.online && System.currentTimeMillis() - peer.lastSeen < 60_000,
                )
            }
            .sortedByDescending { it.lastAt }
    }

    fun messagesFor(conversationId: String): List<ChatMessage> =
        _messages.value.filter { it.conversationId == conversationId }.sortedBy { it.createdAt }

    fun person(id: String): UserProfile? =
        if (_me.value?.id == id) _me.value else _people.value.firstOrNull { it.id == id }

    fun livePeople(): List<UserProfile> {
        val meId = _me.value?.id
        val now = System.currentTimeMillis()
        return _people.value
            .filter { it.id != meId }
            .map { it.copy(online = now - it.lastSeen < 70_000) }
            .sortedWith(compareByDescending<UserProfile> { it.online }.thenBy { it.displayName.lowercase() })
    }

    fun liveStatuses(): List<StatusUpdate> {
        val cutoff = System.currentTimeMillis() - STATUS_TTL_MS
        return _statuses.value.filter { it.createdAt >= cutoff }.sortedByDescending { it.createdAt }
    }

    private fun announceProfile(user: UserProfile) {
        val safe = user.copy(passwordHash = "", online = true, lastSeen = System.currentTimeMillis())
        publish("profile", json.encodeToString(UserProfile.serializer(), safe))
    }

    private fun announcePresence(user: UserProfile? = _me.value) {
        val me = user ?: return
        val ping = PresencePing(
            id = me.id,
            name = me.displayName,
            photoUrl = me.photoUrl,
            city = me.city,
            language = me.language,
            online = me.online,
            lastSeen = System.currentTimeMillis(),
        )
        publish("presence", json.encodeToString(PresencePing.serializer(), ping))
    }

    private fun publish(kind: String, body: String) {
        bus.publish(Envelope(kind = kind, body = body))
    }

    private fun ingest(env: Envelope) {
        try {
            when (env.kind) {
                "profile" -> {
                    val remote = json.decodeFromString(UserProfile.serializer(), env.body)
                    if (remote.id == _me.value?.id) return
                    mergePerson(remote.copy(passwordHash = "", online = true, lastSeen = System.currentTimeMillis()))
                }
                "presence" -> {
                    val ping = json.decodeFromString(PresencePing.serializer(), env.body)
                    if (ping.id == _me.value?.id) return
                    val existing = _people.value.firstOrNull { it.id == ping.id }
                    val merged = (existing ?: UserProfile(
                        id = ping.id,
                        handle = ping.name.lowercase().replace(" ", ""),
                        displayName = ping.name,
                    )).copy(
                        displayName = ping.name.ifBlank { existing?.displayName.orEmpty() },
                        photoUrl = ping.photoUrl.ifBlank { existing?.photoUrl.orEmpty() },
                        city = ping.city.ifBlank { existing?.city.orEmpty() },
                        language = ping.language.ifBlank { existing?.language ?: "en" },
                        online = ping.online,
                        lastSeen = ping.lastSeen,
                    )
                    mergePerson(merged)
                }
                "chat" -> {
                    val msg = json.decodeFromString(ChatMessage.serializer(), env.body)
                    val meId = _me.value?.id
                    if (msg.senderId == meId) return
                    if (meId != null && msg.recipientId.isNotBlank() && msg.recipientId != meId && msg.senderId != meId) return
                    if (_messages.value.any { it.id == msg.id }) return
                    appendMessage(msg)
                }
                "status" -> {
                    val status = json.decodeFromString(StatusUpdate.serializer(), env.body)
                    if (_statuses.value.any { it.id == status.id }) return
                    _statuses.update { listOf(status) + it }
                    persistStatuses()
                }
                "room" -> {
                    val room = json.decodeFromString(VoiceRoom.serializer(), env.body)
                    if (_rooms.value.any { it.id == room.id }) return
                    _rooms.update { listOf(room) + it }
                    persistRooms()
                }
                "clip" -> {
                    val clip = json.decodeFromString(VoiceClip.serializer(), env.body)
                    if (_clips.value.any { it.id == clip.id }) return
                    _clips.update { it + clip }
                    persistClips()
                }
            }
        } catch (e: Exception) {
            Log.w("SalonRepo", "ingest ${env.kind}: ${e.message}")
        }
    }

    private fun mergePerson(remote: UserProfile) {
        _people.update { list ->
            val without = list.filterNot { it.id == remote.id }
            (without + remote).sortedBy { it.displayName.lowercase() }
        }
        persistPeople()
    }

    private fun appendMessage(msg: ChatMessage) {
        _messages.update { it + msg }
        persistMessages()
    }

    private fun upsertLocalUser(user: UserProfile) {
        val all = loadUsers()
        saveUsers(all.filterNot { it.id == user.id } + user)
    }

    private fun persistMe() {
        val me = _me.value ?: return
        File(dir, "session.json").writeText(json.encodeToString(UserProfile.serializer(), me))
    }

    private fun persistPeople() {
        File(dir, "people.json").writeText(
            json.encodeToString(ListSerializer(UserProfile.serializer()), _people.value),
        )
    }

    private fun persistMessages() {
        File(dir, "messages.json").writeText(
            json.encodeToString(ListSerializer(ChatMessage.serializer()), _messages.value),
        )
    }

    private fun persistStatuses() {
        File(dir, "status.json").writeText(
            json.encodeToString(ListSerializer(StatusUpdate.serializer()), _statuses.value),
        )
    }

    private fun persistRooms() {
        File(dir, "rooms.json").writeText(
            json.encodeToString(ListSerializer(VoiceRoom.serializer()), _rooms.value),
        )
    }

    private fun persistClips() {
        File(dir, "clips.json").writeText(
            json.encodeToString(ListSerializer(VoiceClip.serializer()), _clips.value),
        )
    }

    private fun persistAi() {
        File(dir, "ai.json").writeText(
            json.encodeToString(ListSerializer(AiTurn.serializer()), _ai.value),
        )
    }

    private fun loadAll() {
        File(dir, "session.json").takeIf { it.exists() }?.let {
            runCatching { _me.value = json.decodeFromString(UserProfile.serializer(), it.readText()) }
        }
        File(dir, "people.json").takeIf { it.exists() }?.let {
            runCatching {
                _people.value = json.decodeFromString(ListSerializer(UserProfile.serializer()), it.readText())
            }
        }
        File(dir, "messages.json").takeIf { it.exists() }?.let {
            runCatching {
                _messages.value = json.decodeFromString(ListSerializer(ChatMessage.serializer()), it.readText())
            }
        }
        File(dir, "status.json").takeIf { it.exists() }?.let {
            runCatching {
                _statuses.value = json.decodeFromString(ListSerializer(StatusUpdate.serializer()), it.readText())
            }
        }
        File(dir, "rooms.json").takeIf { it.exists() }?.let {
            runCatching {
                _rooms.value = json.decodeFromString(ListSerializer(VoiceRoom.serializer()), it.readText())
            }
        }
        File(dir, "clips.json").takeIf { it.exists() }?.let {
            runCatching {
                _clips.value = json.decodeFromString(ListSerializer(VoiceClip.serializer()), it.readText())
            }
        }
        File(dir, "ai.json").takeIf { it.exists() }?.let {
            runCatching {
                _ai.value = json.decodeFromString(ListSerializer(AiTurn.serializer()), it.readText())
            }
        }
    }

    private fun loadUsers(): List<UserProfile> {
        val f = File(dir, "accounts.json")
        if (!f.exists()) return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(UserProfile.serializer()), f.readText())
        }.getOrDefault(emptyList())
    }

    private fun saveUsers(users: List<UserProfile>) {
        File(dir, "accounts.json").writeText(
            json.encodeToString(ListSerializer(UserProfile.serializer()), users),
        )
    }

    private fun pruneStatuses() {
        val cutoff = System.currentTimeMillis() - STATUS_TTL_MS
        val next = _statuses.value.filter { it.createdAt >= cutoff }
        if (next.size != _statuses.value.size) {
            _statuses.value = next
            persistStatuses()
        }
    }

    private fun hash(raw: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
