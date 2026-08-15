package com.agon.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.SalonApp
import com.agon.app.data.AppSettings
import com.agon.app.data.ChatMessage
import com.agon.app.data.MqttBus
import com.agon.app.data.OnboardingAnswers
import com.agon.app.data.StatusUpdate
import com.agon.app.data.UserProfile
import com.agon.app.data.VoiceRoom
import com.agon.app.data.appLangOf
import com.agon.app.data.stringsFor
import com.agon.app.ui.theme.SalonPalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SalonApp.instance.repository
    private val prefs = SalonApp.instance.prefs

    val settings: StateFlow<AppSettings> = prefs.settings.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AppSettings(),
    )

    val me = repo.me
    val people = repo.people
    val messages = repo.messages
    val statuses = repo.statuses
    val rooms = repo.rooms
    val clips = repo.clips
    val ai = repo.ai
    val link = repo.link

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _notice = MutableStateFlow<String?>(null)
    val notice: StateFlow<String?> = _notice.asStateFlow()

    private val _answers = MutableStateFlow(OnboardingAnswers())
    val answers: StateFlow<OnboardingAnswers> = _answers.asStateFlow()

    val strings = combine(settings) { s -> stringsFor(s[0].language) }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        stringsFor("en"),
    )

    val palette: SalonPalette
        get() = runCatching { SalonPalette.valueOf(settings.value.palette) }
            .getOrDefault(SalonPalette.LEONE_FLAG)

    fun darkPref(): Boolean? = when (settings.value.darkMode) {
        "dark" -> true
        "light" -> false
        else -> null
    }

    fun consumeNotice() {
        _notice.value = null
    }

    fun setAnswer(transform: (OnboardingAnswers) -> OnboardingAnswers) {
        _answers.value = transform(_answers.value)
    }

    fun finishOnboarding() {
        viewModelScope.launch { prefs.setOnboardingDone() }
    }

    fun setHaptic(v: Boolean) = viewModelScope.launch { prefs.setHaptic(v) }
    fun setLanguage(v: String) = viewModelScope.launch { prefs.setLanguage(v) }
    fun setPalette(v: String) = viewModelScope.launch { prefs.setPalette(v) }
    fun setDarkMode(v: String) = viewModelScope.launch { prefs.setDarkMode(v) }
    fun setAutoplay(v: Boolean) = viewModelScope.launch { prefs.setAutoplay(v) }
    fun setNotify(v: Boolean) = viewModelScope.launch { prefs.setNotify(v) }

    fun register(
        handle: String,
        displayName: String,
        password: String,
        phone: String,
        email: String,
        city: String,
        district: String,
        region: String,
        tribe: String,
        language: String,
        bio: String,
        favoriteDish: String,
        purpose: String,
        onDone: (Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            _busy.value = true
            val seed = _answers.value
            val profile = UserProfile(
                id = "",
                handle = handle,
                displayName = displayName.ifBlank { seed.displayName }.ifBlank { handle },
                phone = phone,
                email = email,
                city = city,
                district = district,
                region = region.ifBlank { seed.region },
                tribe = tribe,
                language = language.ifBlank { seed.language },
                bio = bio,
                favoriteDish = favoriteDish.ifBlank { seed.favoriteDish },
                purpose = purpose.ifBlank { seed.purpose },
            )
            val result = repo.register(profile, password)
            _busy.value = false
            result.onSuccess {
                prefs.setRegistered(true)
                prefs.setSession(it.id)
                prefs.setLanguage(it.language)
                onDone(true)
            }.onFailure {
                _notice.value = it.message
                onDone(false)
            }
        }
    }

    fun login(handle: String, password: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _busy.value = true
            val result = repo.login(handle, password)
            _busy.value = false
            result.onSuccess {
                prefs.setRegistered(true)
                prefs.setSession(it.id)
                prefs.setLanguage(it.language)
                onDone(true)
            }.onFailure {
                _notice.value = it.message
                onDone(false)
            }
        }
    }

    fun restoreIfNeeded() {
        viewModelScope.launch {
            if (me.value != null) return@launch
            prefs.sessionUserId.collect { id ->
                if (id.isNotBlank() && me.value == null) repo.restoreSession(id)
            }
        }
    }

    fun saveProfile(updated: UserProfile, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.updateProfile { updated.copy(id = it.id, handle = it.handle, passwordHash = it.passwordHash) }
            prefs.setLanguage(updated.language)
            _notice.value = "saved"
            onDone()
        }
    }

    fun uploadAvatar(uri: Uri, onDone: () -> Unit) {
        viewModelScope.launch {
            _busy.value = true
            runCatching {
                val url = com.agon.app.data.ImageHost.upload(getApplication(), uri, "avatar_${System.currentTimeMillis()}.jpg")
                repo.updateProfile { it.copy(photoUrl = url) }
            }.onFailure { _notice.value = it.message }
            _busy.value = false
            onDone()
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repo.signOut()
            prefs.clearSession()
        }
    }

    fun sendText(to: UserProfile, text: String) {
        viewModelScope.launch {
            runCatching { repo.sendText(to, text) }
                .onFailure { _notice.value = it.message }
        }
    }

    fun sendMedia(to: UserProfile, uri: Uri, type: String, caption: String = "") {
        viewModelScope.launch {
            _busy.value = true
            runCatching { repo.sendMedia(to, uri, type, caption) }
                .onFailure { _notice.value = it.message }
            _busy.value = false
        }
    }

    fun sendVoice(to: UserProfile, file: File, duration: Long) {
        viewModelScope.launch {
            _busy.value = true
            runCatching { repo.sendVoiceNote(to, file, duration) }
                .onFailure { _notice.value = it.message }
            _busy.value = false
        }
    }

    fun postStatus(text: String, uri: Uri?, type: String) {
        viewModelScope.launch {
            _busy.value = true
            runCatching { repo.postStatus(text, uri, type) }
                .onFailure { _notice.value = it.message }
            _busy.value = false
        }
    }

    fun createRoom(title: String, topic: String) {
        viewModelScope.launch { repo.createRoom(title, topic) }
    }

    fun sendRoomClip(room: VoiceRoom, file: File, duration: Long) {
        viewModelScope.launch {
            _busy.value = true
            runCatching { repo.sendRoomClip(room, file, duration) }
                .onFailure { _notice.value = it.message }
            _busy.value = false
        }
    }

    fun ask(question: String) = repo.askAi(question)

    fun conversations() = repo.conversations()
    fun thread(id: String): List<ChatMessage> = repo.messagesFor(id)
    fun person(id: String): UserProfile? = repo.person(id)
    fun livePeople(): List<UserProfile> = repo.livePeople()
    fun liveStatuses(): List<StatusUpdate> = repo.liveStatuses()
    fun clipsFor(roomId: String) = clips.value.filter { it.roomId == roomId }

    fun linkLabel(): String = when (link.value) {
        MqttBus.Link.Live -> strings.value.connected
        MqttBus.Link.Connecting -> strings.value.connecting
        MqttBus.Link.Offline -> strings.value.offline
    }

    fun langName() = appLangOf(settings.value.language).nativeName
}
