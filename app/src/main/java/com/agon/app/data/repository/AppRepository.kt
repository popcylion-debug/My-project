package com.agon.app.data.repository

import com.agon.app.data.models.*
import com.agon.app.ui.theme.SaloneThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppRepository(
    val dbEngine: SalonDatabaseEngine = SalonDatabaseEngine()
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    // Current Authenticated User (Starts with authentic initial user from DB, user can log out or switch anytime)
    private val _currentUser = MutableStateFlow<User>(
        dbEngine.registeredUsers.value.first()
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Preferences & Settings
    private val _privacySettings = MutableStateFlow(PrivacySettings())
    val privacySettings: StateFlow<PrivacySettings> = _privacySettings.asStateFlow()

    private val _notificationPrefs = MutableStateFlow(NotificationPreferences())
    val notificationPrefs: StateFlow<NotificationPreferences> = _notificationPrefs.asStateFlow()

    private val _currentTheme = MutableStateFlow(SaloneThemeMode.COTTON_TREE_GREEN)
    val currentTheme: StateFlow<SaloneThemeMode> = _currentTheme.asStateFlow()

    private val _currentLanguage = MutableStateFlow(SaloneLanguage.ENGLISH)
    val currentLanguage: StateFlow<SaloneLanguage> = _currentLanguage.asStateFlow()

    private val _fontScale = MutableStateFlow(1.0f)
    val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _accountTier = MutableStateFlow(AccountTier.VIP_PLUS)
    val accountTier: StateFlow<AccountTier> = _accountTier.asStateFlow()

    // Active Call
    private val _activeCall = MutableStateFlow<ActiveCallState?>(null)
    val activeCall: StateFlow<ActiveCallState?> = _activeCall.asStateFlow()

    // In-app Notification Banner
    private val _inAppNotification = MutableStateFlow<String?>(null)
    val inAppNotification: StateFlow<String?> = _inAppNotification.asStateFlow()

    // All Users Directory (Synchronized with SalonDatabaseEngine)
    val allUsers: StateFlow<List<User>> = dbEngine.registeredUsers

    // Blocked Users
    private val _blockedUsers = MutableStateFlow<List<User>>(emptyList())
    val blockedUsers: StateFlow<List<User>> = _blockedUsers.asStateFlow()

    // Contacts
    private val _contacts = MutableStateFlow<List<ContactItem>>(emptyList())
    val contacts: StateFlow<List<ContactItem>> = _contacts.asStateFlow()

    // Chats
    private val _chats = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chats: StateFlow<List<ChatRoom>> = _chats.asStateFlow()

    // Messages grouped by chatId
    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messages: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()

    // Stories / Status
    private val _stories = MutableStateFlow<List<StoryItem>>(emptyList())
    val stories: StateFlow<List<StoryItem>> = _stories.asStateFlow()

    // Calls History
    private val _callHistory = MutableStateFlow<List<CallRecord>>(emptyList())
    val callHistory: StateFlow<List<CallRecord>> = _callHistory.asStateFlow()

    // Admin Data
    private val _adminAnalytics = MutableStateFlow(SystemAnalytics())
    val adminAnalytics: StateFlow<SystemAnalytics> = _adminAnalytics.asStateFlow()

    private val _adminReports = MutableStateFlow<List<ReportItem>>(createInitialReports())
    val adminReports: StateFlow<List<ReportItem>> = _adminReports.asStateFlow()

    private val _systemAnnouncements = MutableStateFlow<List<SystemAnnouncement>>(createInitialAnnouncements())
    val systemAnnouncements: StateFlow<List<SystemAnnouncement>> = _systemAnnouncements.asStateFlow()

    // Business Data
    private val _businessProfile = MutableStateFlow(BusinessProfileData())
    val businessProfile: StateFlow<BusinessProfileData> = _businessProfile.asStateFlow()

    private val _chatBusinessMeta = MutableStateFlow<Map<String, ChatBusinessMeta>>(emptyMap())
    val chatBusinessMeta: StateFlow<Map<String, ChatBusinessMeta>> = _chatBusinessMeta.asStateFlow()

    init {
        initializeData()
    }

    private fun initializeData() {
        val users = dbEngine.registeredUsers.value
        _contacts.value = users.map { ContactItem(user = it, isFavorite = it.isVerified) }

        val initialChats = createInitialChats(users)
        _chats.value = initialChats

        val initialMessages = createInitialMessages(initialChats, users)
        _messages.value = initialMessages

        _stories.value = createInitialStories(users)
        _callHistory.value = createInitialCalls(users)
    }

    // AUTH ACTIONS
    fun login(identifier: String, pass: String): Boolean {
        val authenticatedUser = dbEngine.authenticateUser(identifier, pass)
        if (authenticatedUser != null) {
            _currentUser.value = authenticatedUser
            _isLoggedIn.value = true
            showToast("Welcome back, ${authenticatedUser.displayName}! 🇸🇱")
            return true
        } else {
            // Register or login directly with custom identifier
            val name = if (identifier.contains("@")) identifier.substringBefore("@") else identifier
            val newUser = dbEngine.registerNewAccount(
                name = name.replace("_", " ").replaceFirstChar { it.uppercase() },
                username = identifier.lowercase().replace(" ", "_"),
                phone = if (identifier.startsWith("+")) identifier else "+232 76 " + (100000..999999).random(),
                email = if (identifier.contains("@")) identifier else "$identifier@salonnaweyon.sl",
                password = pass,
                tribeOrRegion = "Freetown (Western Area)"
            )
            _currentUser.value = newUser
            _isLoggedIn.value = true
            showToast("Welcome to Salon Na We Yon, ${newUser.displayName}!")
            return true
        }
    }

    fun signUp(name: String, username: String, phone: String, email: String, tribe: String): Boolean {
        val cleanUsername = username.trim().removePrefix("@").lowercase().replace(" ", "_")
        val newUser = dbEngine.registerNewAccount(
            name = name.trim(),
            username = cleanUsername,
            phone = phone.trim(),
            email = email.trim(),
            password = "Salone@2025",
            tribeOrRegion = tribe
        )
        _currentUser.value = newUser
        _isLoggedIn.value = true
        showToast("Account created successfully! Kusheh, ${newUser.displayName}! 🇸🇱")
        return true
    }

    fun switchAccount(user: User) {
        _currentUser.value = user
        _isLoggedIn.value = true
        showToast("Switched active profile to ${user.displayName}")
    }

    fun logout() {
        _isLoggedIn.value = false
        showToast("Logged out of Salon Na We Yon")
    }

    fun deleteAccount() {
        val me = _currentUser.value
        dbEngine.deleteUserAccount(me.id)
        _isLoggedIn.value = false
        showToast("Account deleted successfully.")
    }

    fun updateProfile(name: String, username: String, bio: String, phone: String, email: String, avatarColor: String, tribe: String) {
        val updated = _currentUser.value.copy(
            displayName = name,
            username = username,
            bio = bio,
            phoneNumber = phone,
            email = email,
            avatarColorHex = avatarColor,
            tribeOrLocation = tribe
        )
        _currentUser.value = updated
        dbEngine.updateUserProfile(updated)
        showToast("Profile updated successfully!")
    }

    fun toggleOnlineStatus(isOnline: Boolean) {
        _currentUser.update { it.copy(isOnline = isOnline, lastSeenText = if (isOnline) "Online" else "Last seen just now") }
    }

    // PREFERENCES & PRIVACY
    fun updatePrivacySettings(newSettings: PrivacySettings) {
        _privacySettings.value = newSettings
        showToast("Privacy settings saved")
    }

    fun updateNotificationPrefs(newPrefs: NotificationPreferences) {
        _notificationPrefs.value = newPrefs
        showToast("Notification settings saved")
    }

    fun setTheme(theme: SaloneThemeMode) {
        _currentTheme.value = theme
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun setLanguage(lang: SaloneLanguage) {
        _currentLanguage.value = lang
        showToast("Language changed to ${lang.displayName}")
    }

    fun setFontScale(scale: Float) {
        _fontScale.value = scale
    }

    fun upgradeAccount(tier: AccountTier, paymentMethod: String) {
        _accountTier.value = tier
        val updated = _currentUser.value.copy(isVip = true, isBusiness = tier == AccountTier.BUSINESS_PRO)
        _currentUser.value = updated
        dbEngine.updateUserProfile(updated)
        showToast("Upgraded to ${tier.name} via $paymentMethod! Gold VIP crest activated.")
    }

    // CHAT ACTIONS
    fun sendMessage(
        chatId: String,
        text: String,
        type: MessageType = MessageType.TEXT,
        mediaUrl: String? = null,
        caption: String? = null,
        replyTo: ReplyReference? = null,
        locationData: LocationPayload? = null,
        contactData: ContactPayload? = null,
        documentData: DocumentPayload? = null,
        audioData: AudioPayload? = null
    ) {
        val now = System.currentTimeMillis()
        val formattedTime = timeFormat.format(Date(now))
        val user = _currentUser.value

        val newMsg = Message(
            id = "msg_${UUID.randomUUID()}",
            chatId = chatId,
            senderId = user.id,
            senderName = user.displayName,
            senderAvatar = user.avatarUrl,
            isOutgoing = true,
            text = text,
            type = type,
            timestamp = now,
            timeFormatted = formattedTime,
            status = MessageStatus.READ,
            mediaUrl = mediaUrl,
            mediaCaption = caption,
            replyTo = replyTo,
            locationData = locationData,
            contactData = contactData,
            documentData = documentData,
            audioData = audioData
        )

        // Add message
        val currentList = _messages.value[chatId] ?: emptyList()
        _messages.update { it + (chatId to (currentList + newMsg)) }

        // Update chat room last message
        _chats.update { list ->
            list.map { room ->
                if (room.id == chatId) {
                    room.copy(lastMessage = newMsg)
                } else room
            }
        }

        // AI Bot or Automated Peer Response
        val chat = _chats.value.find { it.id == chatId }
        if (chat?.category == ChatCategory.AI_BOT) {
            handleAiChatbotResponse(chatId, text)
        } else if (chat?.category == ChatCategory.DIRECT) {
            simulateAutoReply(chatId, chat.directUser?.displayName ?: "Friend")
        }
    }

    fun editMessage(chatId: String, messageId: String, newText: String) {
        val list = _messages.value[chatId] ?: return
        val updated = list.map { msg ->
            if (msg.id == messageId) {
                msg.copy(text = newText, isEdited = true)
            } else msg
        }
        _messages.update { it + (chatId to updated) }
        showToast("Message edited")
    }

    fun deleteMessage(chatId: String, messageId: String, forEveryone: Boolean) {
        val list = _messages.value[chatId] ?: return
        val updated = list.filterNot { it.id == messageId }
        _messages.update { it + (chatId to updated) }
        showToast(if (forEveryone) "Message deleted for everyone" else "Message deleted for you")
    }

    fun addReaction(chatId: String, messageId: String, emoji: String) {
        val list = _messages.value[chatId] ?: return
        val myId = _currentUser.value.id
        val updated = list.map { msg ->
            if (msg.id == messageId) {
                val existingReactions = msg.reactions.toMutableList()
                val idx = existingReactions.indexOfFirst { it.emoji == emoji }
                if (idx >= 0) {
                    val current = existingReactions[idx]
                    val userHasReacted = current.userIds.contains(myId)
                    if (userHasReacted) {
                        val newUserIds = current.userIds - myId
                        if (newUserIds.isEmpty()) {
                            existingReactions.removeAt(idx)
                        } else {
                            existingReactions[idx] = current.copy(count = newUserIds.size, userIds = newUserIds, hasReacted = false)
                        }
                    } else {
                        val newUserIds = current.userIds + myId
                        existingReactions[idx] = current.copy(count = newUserIds.size, userIds = newUserIds, hasReacted = true)
                    }
                } else {
                    existingReactions.add(MessageReaction(emoji = emoji, count = 1, userIds = listOf(myId), hasReacted = true))
                }
                msg.copy(reactions = existingReactions)
            } else msg
        }
        _messages.update { it + (chatId to updated) }
    }

    fun toggleStarMessage(chatId: String, messageId: String) {
        val list = _messages.value[chatId] ?: return
        val updated = list.map { msg ->
            if (msg.id == messageId) {
                val newStarred = !msg.isStarred
                showToast(if (newStarred) "Message starred ⭐" else "Message unstarred")
                msg.copy(isStarred = newStarred)
            } else msg
        }
        _messages.update { it + (chatId to updated) }
    }

    fun togglePinMessage(chatId: String, messageId: String) {
        val list = _messages.value[chatId] ?: return
        val targetMsg = list.find { it.id == messageId } ?: return
        val isNowPinned = !targetMsg.isPinned

        val updated = list.map { msg ->
            if (msg.id == messageId) {
                msg.copy(isPinned = isNowPinned)
            } else msg
        }
        _messages.update { it + (chatId to updated) }

        _chats.update { rooms ->
            rooms.map { room ->
                if (room.id == chatId) {
                    room.copy(pinnedMessage = if (isNowPinned) targetMsg.copy(isPinned = true) else null)
                } else room
            }
        }
        showToast(if (isNowPinned) "Message pinned to top 📌" else "Message unpinned")
    }

    fun forwardMessage(sourceChatId: String, targetChatId: String, messageId: String) {
        val msg = _messages.value[sourceChatId]?.find { it.id == messageId } ?: return
        sendMessage(
            chatId = targetChatId,
            text = msg.text,
            type = msg.type,
            mediaUrl = msg.mediaUrl,
            locationData = msg.locationData,
            contactData = msg.contactData,
            documentData = msg.documentData,
            audioData = msg.audioData
        )
        showToast("Forwarded message successfully!")
    }

    fun createGroup(name: String, description: String, selectedUserIds: List<String>, isAnnouncementsOnly: Boolean): String {
        val newGroupId = "group_${System.currentTimeMillis()}"
        val myMember = GroupMember(user = _currentUser.value, role = MemberRole.OWNER)
        val otherMembers = dbEngine.registeredUsers.value.filter { selectedUserIds.contains(it.id) }.map {
            GroupMember(user = it, role = MemberRole.MEMBER)
        }

        val newRoom = ChatRoom(
            id = newGroupId,
            name = name,
            category = ChatCategory.GROUP,
            avatarColorHex = listOf("#008751", "#0066B2", "#FBB034").random(),
            groupDescription = description,
            members = listOf(myMember) + otherMembers,
            isAnnouncementsOnly = isAnnouncementsOnly
        )

        _chats.update { listOf(newRoom) + it }

        val welcomeMsg = Message(
            id = "sys_${System.currentTimeMillis()}",
            chatId = newGroupId,
            senderId = "system",
            senderName = "System",
            isOutgoing = false,
            text = "Group '$name' created. Welcome everyone to Salon Na We Yon! 🇸🇱",
            type = MessageType.SYSTEM_NOTICE,
            timeFormatted = timeFormat.format(Date())
        )
        _messages.update { it + (newGroupId to listOf(welcomeMsg)) }
        showToast("Group '$name' created with ${otherMembers.size + 1} members!")
        return newGroupId
    }

    fun deleteGroup(chatId: String) {
        _chats.update { it.filterNot { room -> room.id == chatId } }
        _messages.update { it - chatId }
        showToast("Group deleted")
    }

    fun updateGroupInfo(chatId: String, name: String, description: String, rules: String, isAnnouncementsOnly: Boolean) {
        _chats.update { list ->
            list.map { room ->
                if (room.id == chatId) {
                    room.copy(
                        name = name,
                        groupDescription = description,
                        groupRules = rules,
                        isAnnouncementsOnly = isAnnouncementsOnly
                    )
                } else room
            }
        }
        showToast("Group info updated!")
    }

    fun addGroupMember(chatId: String, user: User) {
        _chats.update { list ->
            list.map { room ->
                if (room.id == chatId && room.members.none { it.user.id == user.id }) {
                    val updatedMembers = room.members + GroupMember(user = user, role = MemberRole.MEMBER)
                    room.copy(members = updatedMembers)
                } else room
            }
        }
        showToast("${user.displayName} added to group")
    }

    fun removeGroupMember(chatId: String, userId: String) {
        _chats.update { list ->
            list.map { room ->
                if (room.id == chatId) {
                    val updatedMembers = room.members.filterNot { it.user.id == userId }
                    room.copy(members = updatedMembers)
                } else room
            }
        }
        showToast("Member removed")
    }

    fun updateMemberRole(chatId: String, userId: String, role: MemberRole) {
        _chats.update { list ->
            list.map { room ->
                if (room.id == chatId) {
                    val updated = room.members.map {
                        if (it.user.id == userId) it.copy(role = role) else it
                    }
                    room.copy(members = updated)
                } else room
            }
        }
        showToast("Member role updated to $role")
    }

    fun toggleSlowMode(chatId: String, seconds: Int) {
        _chats.update { list ->
            list.map { room ->
                if (room.id == chatId) {
                    val perms = room.permissions.copy(slowModeSeconds = seconds)
                    room.copy(permissions = perms)
                } else room
            }
        }
        showToast(if (seconds > 0) "Slow mode set to ${seconds}s" else "Slow mode disabled")
    }

    fun toggleMuteChat(chatId: String, duration: String) {
        _chats.update { list ->
            list.map { room ->
                if (room.id == chatId) {
                    val newMuted = !room.isMuted
                    room.copy(isMuted = newMuted, muteUntil = if (newMuted) duration else null)
                } else room
            }
        }
        showToast("Chat notification settings updated")
    }

    // STORIES / STATUS
    fun addStory(text: String, gradientHex: List<String>, mediaUrl: String? = null, caption: String? = null) {
        val user = _currentUser.value
        val newStory = StoryItem(
            id = "story_${System.currentTimeMillis()}",
            userId = user.id,
            userName = user.displayName,
            userAvatar = user.avatarUrl,
            userAvatarColor = user.avatarColorHex,
            timestamp = System.currentTimeMillis(),
            timeAgoFormatted = "Just now",
            mediaUrl = mediaUrl,
            textContent = text,
            backgroundGradientHex = gradientHex,
            caption = caption,
            isMine = true,
            isSeenByMe = true,
            viewers = emptyList()
        )
        _stories.update { listOf(newStory) + it }
        showToast("Status posted! Visible for 24 hours 🇸🇱")
    }

    fun recordStoryView(storyId: String) {
        val me = _currentUser.value
        _stories.update { list ->
            list.map { story ->
                if (story.id == storyId) {
                    val alreadyViewed = story.viewers.any { it.user.id == me.id }
                    val updatedViewers = if (!alreadyViewed) {
                        story.viewers + StoryViewer(user = me, viewedAt = "Just now")
                    } else story.viewers
                    story.copy(isSeenByMe = true, viewers = updatedViewers)
                } else story
            }
        }
    }

    fun reactToStory(storyId: String, emoji: String) {
        val me = _currentUser.value
        _stories.update { list ->
            list.map { story ->
                if (story.id == storyId) {
                    val updated = story.viewers.map {
                        if (it.user.id == me.id) it.copy(reactionEmoji = emoji) else it
                    }
                    story.copy(viewers = updated)
                } else story
            }
        }
        showToast("Reacted $emoji to status!")
    }

    // CALLS
    fun startCall(peerUser: User, mediaType: CallMediaType, isGroup: Boolean = false, groupName: String? = null) {
        val newCall = ActiveCallState(
            callId = "call_${System.currentTimeMillis()}",
            peerUser = peerUser,
            isGroupCall = isGroup,
            groupName = groupName,
            participants = if (isGroup) listOf(_currentUser.value, peerUser) else emptyList(),
            mediaType = mediaType,
            durationSeconds = 0,
            isRinging = true
        )
        _activeCall.value = newCall

        // Start call duration timer
        scope.launch {
            delay(2000)
            _activeCall.update { it?.copy(isRinging = false) }
            while (_activeCall.value != null) {
                delay(1000)
                _activeCall.update { state ->
                    state?.copy(durationSeconds = (state.durationSeconds + 1))
                }
            }
        }

        // Add to history
        val record = CallRecord(
            id = "call_rec_${System.currentTimeMillis()}",
            user = peerUser,
            isGroupCall = isGroup,
            groupName = groupName,
            mediaType = mediaType,
            direction = CallDirection.OUTGOING,
            timeFormatted = "Just now",
            durationFormatted = "Ongoing"
        )
        _callHistory.update { listOf(record) + it }
    }

    fun endCall() {
        _activeCall.value = null
        showToast("Call ended")
    }

    fun toggleCallMute() {
        _activeCall.update { it?.copy(isMuted = !(it.isMuted)) }
    }

    fun toggleCallCamera() {
        _activeCall.update { it?.copy(isCameraOff = !(it.isCameraOff)) }
    }

    fun toggleCallSpeaker() {
        _activeCall.update { it?.copy(isSpeakerOn = !(it.isSpeakerOn)) }
    }

    fun toggleScreenShare() {
        _activeCall.update {
            val nextState = !(it?.isScreenSharing ?: false)
            showToast(if (nextState) "Screen sharing active 🖥️" else "Screen sharing stopped")
            it?.copy(isScreenSharing = nextState)
        }
    }

    fun flipCamera() {
        _activeCall.update { it?.copy(isFrontCamera = !(it.isFrontCamera)) }
    }

    fun simulateIncomingCall(fromUser: User, mediaType: CallMediaType) {
        val newCall = ActiveCallState(
            callId = "call_inc_${System.currentTimeMillis()}",
            peerUser = fromUser,
            mediaType = mediaType,
            durationSeconds = 0,
            isRinging = true
        )
        _activeCall.value = newCall
    }

    // CONTACTS & DISCOVERY
    fun addContact(name: String, phone: String, tribe: String): User {
        val newUser = dbEngine.registerNewAccount(
            name = name,
            username = name.lowercase().replace(" ", "_"),
            phone = phone,
            email = "${name.lowercase().replace(" ", "")}@salone.sl",
            password = "Salone@2025",
            tribeOrRegion = tribe
        )
        _contacts.update { it + ContactItem(user = newUser) }
        showToast("Added $name to contacts!")
        return newUser
    }

    fun getOrCreateDirectChat(user: User): String {
        val existing = _chats.value.find { it.category == ChatCategory.DIRECT && it.directUser?.id == user.id }
        if (existing != null) return existing.id

        val newChatId = "chat_direct_${user.id}"
        val newRoom = ChatRoom(
            id = newChatId,
            name = user.displayName,
            category = ChatCategory.DIRECT,
            avatarColorHex = user.avatarColorHex,
            directUser = user
        )
        _chats.update { listOf(newRoom) + it }
        _messages.update { it + (newChatId to emptyList()) }
        return newChatId
    }

    fun blockUser(userId: String) {
        val user = dbEngine.findUserById(userId) ?: return
        _blockedUsers.update { if (!it.any { u -> u.id == userId }) it + user else it }
        showToast("${user.displayName} blocked")
    }

    fun unblockUser(userId: String) {
        _blockedUsers.update { it.filterNot { u -> u.id == userId } }
        showToast("User unblocked")
    }

    fun reportUser(reportedUserId: String, messageSnippet: String, reason: String) {
        val repUser = dbEngine.findUserById(reportedUserId)
        val newReport = ReportItem(
            id = "rep_${System.currentTimeMillis()}",
            reporterId = _currentUser.value.id,
            reporterName = _currentUser.value.displayName,
            reportedUserId = reportedUserId,
            reportedUserName = repUser?.displayName ?: "User",
            reportedMessageSnippet = messageSnippet,
            reason = reason,
            timestamp = "Just now"
        )
        _adminReports.update { listOf(newReport) + it }
        showToast("Report submitted to Salon Na We Yon Moderation Team. Thank you!")
    }

    // AI FEATURES
    private fun handleAiChatbotResponse(chatId: String, query: String) {
        scope.launch {
            _chats.update { list ->
                list.map { if (it.id == chatId) it.copy(isTyping = true, typingUserName = "KrioGPT AI") else it }
            }
            delay(1400)

            val q = query.lowercase()
            val aiResponse = when {
                q.contains("kusheh") || q.contains("hello") || q.contains("hi") ->
                    "Kusheh broda/sista! 🇸🇱 Salon Na We Yon AI dey ya for help yu with translations, cultural history, business drafts & tech advice!"
                q.contains("freetown") || q.contains("cotton tree") ->
                    "Freetown is the capital of Sierra Leone, founded in 1792. The famous Cotton Tree stands in the heart of Freetown as a proud symbol of freedom and resilience!"
                q.contains("translate") || q.contains("krio") ->
                    "Krio Translation Guide: \n• 'How are you?' -> 'Aw yu do?'\n• 'I am good' -> 'A de wel, tenki'\n• 'How much is this?' -> 'Ommos na dis?'\n• 'See you later' -> 'Wi go si bambai!'"
                q.contains("mende") || q.contains("temne") ->
                    "Mende: 'Bua' (Hello), 'Bi lei?' (What is your name?)\nTemne: 'Séké' (Hello), 'Kəla?' (How are you?)"
                q.contains("food") || q.contains("cassava") || q.contains("plasas") ->
                    "Sierra Leone dishes: Delicious Cassava leaf plasas, Groundnut soup, Jollof rice, and sweet Fry-Fry (Akara & Fried Plantains) with pepper sauce! 🍲"
                else ->
                    "I am your Salon Na We Yon AI assistant. You asked: '$query'. I can assist with Sierra Leone cultural insights, real-time dialect translations, conversation summaries, and smart business advice!"
            }

            _chats.update { list ->
                list.map { if (it.id == chatId) it.copy(isTyping = false, typingUserName = null) else it }
            }

            val replyMsg = Message(
                id = "ai_msg_${System.currentTimeMillis()}",
                chatId = chatId,
                senderId = "user_kriogpt",
                senderName = "KrioGPT AI Assistant 🇸🇱",
                isOutgoing = false,
                text = aiResponse,
                type = MessageType.TEXT,
                timeFormatted = timeFormat.format(Date()),
                isAiGenerated = true
            )
            val current = _messages.value[chatId] ?: emptyList()
            _messages.update { it + (chatId to (current + replyMsg)) }
        }
    }

    private fun simulateAutoReply(chatId: String, senderName: String) {
        scope.launch {
            delay(2000)
            _chats.update { list ->
                list.map { if (it.id == chatId) it.copy(isTyping = true, typingUserName = senderName) else it }
            }
            delay(2200)
            _chats.update { list ->
                list.map { if (it.id == chatId) it.copy(isTyping = false, typingUserName = null) else it }
            }

            val replies = listOf(
                "Kusheh Joseph! Everything cool on my side. Salon Na We Yon app is super fast! 🇸🇱",
                "Yes! Let's meet at Aberdeen Beach near Lumley this weekend.",
                "Got your message. I'll send you the document right away!",
                "Tenki plenti bro! Let's keep in touch.",
                "Sounds like a great plan! 👍🔥"
            )

            val replyMsg = Message(
                id = "msg_reply_${System.currentTimeMillis()}",
                chatId = chatId,
                senderId = "peer_${chatId}",
                senderName = senderName,
                isOutgoing = false,
                text = replies.random(),
                type = MessageType.TEXT,
                timeFormatted = timeFormat.format(Date())
            )
            val current = _messages.value[chatId] ?: emptyList()
            _messages.update { it + (chatId to (current + replyMsg)) }
        }
    }

    fun summarizeConversation(chatId: String): String {
        val list = _messages.value[chatId] ?: return "No messages to summarize."
        return """
            ✨ AI Conversation Summary:
            • Discussed Sierra Leone project coordination and weekend meetups.
            • Key action item: Review shared documents and schedule Lumley Beach sync.
            • Tone: Positive, productive & collaborative 🇸🇱
            • Total messages exchanged: ${list.size}
        """.trimIndent()
    }

    fun translateMessageToKrio(chatId: String, messageId: String) {
        val list = _messages.value[chatId] ?: return
        val updated = list.map { msg ->
            if (msg.id == messageId) {
                val krio = "🇸🇱 [Krio Translation]: '${msg.text}' -> Kusheh! Tok am well-well na Salone!"
                msg.copy(translatedText = krio)
            } else msg
        }
        _messages.update { it + (chatId to updated) }
        showToast("Translated message to Krio!")
    }

    // ADMIN ACTIONS
    fun adminBanUser(userId: String) {
        val user = dbEngine.findUserById(userId) ?: return
        dbEngine.updateUserProfile(user.copy(isBlocked = true))
        _adminAnalytics.update { it.copy(bannedAccountsCount = it.bannedAccountsCount + 1) }
        showToast("User account banned from network.")
    }

    fun adminUnbanUser(userId: String) {
        val user = dbEngine.findUserById(userId) ?: return
        dbEngine.updateUserProfile(user.copy(isBlocked = false))
        _adminAnalytics.update { it.copy(bannedAccountsCount = (it.bannedAccountsCount - 1).coerceAtLeast(0)) }
        showToast("User account unbanned.")
    }

    fun adminToggleVerifyBadge(userId: String) {
        val user = dbEngine.findUserById(userId) ?: return
        val updated = user.copy(isVerified = !user.isVerified)
        dbEngine.updateUserProfile(updated)
        showToast("Verification badge toggled for ${user.displayName}")
    }

    fun adminResolveReport(reportId: String, newStatus: ReportStatus) {
        _adminReports.update { list ->
            list.map { if (it.id == reportId) it.copy(status = newStatus) else it }
        }
        _adminAnalytics.update { it.copy(pendingReportsCount = (it.pendingReportsCount - 1).coerceAtLeast(0)) }
        showToast("Report marked as $newStatus")
    }

    fun adminBroadcastAnnouncement(title: String, content: String) {
        val announcement = SystemAnnouncement(
            id = "ann_${System.currentTimeMillis()}",
            title = title,
            content = content,
            timestamp = "Just now",
            isPriority = true
        )
        _systemAnnouncements.update { listOf(announcement) + it }
        showToast("System announcement broadcasted to all 248k users! 📢")
    }

    // BUSINESS HUB ACTIONS
    fun updateBusinessProfile(profile: BusinessProfileData) {
        _businessProfile.value = profile
        showToast("Business profile updated!")
    }

    fun updateChatBusinessMeta(chatId: String, agent: String, label: String, notes: String) {
        val meta = ChatBusinessMeta(chatId = chatId, assignedAgent = agent, activeLabel = label, staffNotes = notes)
        _chatBusinessMeta.update { it + (chatId to meta) }
        showToast("Customer tags & notes updated")
    }

    private fun showToast(text: String) {
        _inAppNotification.value = text
        scope.launch {
            delay(3500)
            if (_inAppNotification.value == text) {
                _inAppNotification.value = null
            }
        }
    }

    fun clearInAppNotification() {
        _inAppNotification.value = null
    }

    // INITIAL CHATS, MESSAGES, STORIES & CALLS
    private fun createInitialChats(users: List<User>): List<ChatRoom> {
        val aminata = users.find { it.id == "user_aminata" } ?: users[1]
        val mohamed = users.find { it.id == "user_mohamed" } ?: users[2]
        val fatmata = users.find { it.id == "user_fatmata" } ?: users[3]
        val samuel = users.find { it.id == "user_samuel" } ?: users[4]
        val krioGpt = users.find { it.id == "user_kriogpt" } ?: users.last()

        return listOf(
            ChatRoom(
                id = "chat_kriogpt",
                name = "KrioGPT AI Assistant 🇸🇱",
                category = ChatCategory.AI_BOT,
                avatarColorHex = "#008751",
                directUser = krioGpt,
                isPinned = true,
                unreadCount = 1
            ),
            ChatRoom(
                id = "chat_group_salone_devs",
                name = "🇸🇱 Salone Tech & Innovators",
                category = ChatCategory.GROUP,
                avatarColorHex = "#0066B2",
                groupDescription = "The premier developer and tech community connecting Sierra Leoneans locally and across the diaspora.",
                members = users.map { GroupMember(user = it, role = if (it.id == "user_aminata") MemberRole.ADMIN else MemberRole.MEMBER) },
                isPinned = true,
                unreadCount = 3
            ),
            ChatRoom(
                id = "chat_aminata",
                name = "Aminata Bangura",
                category = ChatCategory.DIRECT,
                avatarColorHex = aminata.avatarColorHex,
                directUser = aminata,
                unreadCount = 0
            ),
            ChatRoom(
                id = "chat_group_marketplace",
                name = "🛍️ Leone Marketplace Freetown",
                category = ChatCategory.GROUP,
                avatarColorHex = "#FBB034",
                groupDescription = "Verified buyers and sellers across Freetown, Bo, Kenema and Makeni. Orange Money & AfriMoney accepted.",
                members = users.map { GroupMember(user = it) },
                unreadCount = 2
            ),
            ChatRoom(
                id = "chat_mohamed",
                name = "Mohamed Turay",
                category = ChatCategory.DIRECT,
                avatarColorHex = mohamed.avatarColorHex,
                directUser = mohamed,
                unreadCount = 0
            ),
            ChatRoom(
                id = "chat_group_freetown_pulse",
                name = "🌴 Freetown Pulse & Culture",
                category = ChatCategory.CHANNEL,
                avatarColorHex = "#008751",
                groupDescription = "Official events, live music, beach hangouts and Sierra Leone cultural entertainment feed.",
                members = users.map { GroupMember(user = it) },
                isAnnouncementsOnly = true,
                unreadCount = 0
            ),
            ChatRoom(
                id = "chat_fatmata",
                name = "Fatmata Koroma",
                category = ChatCategory.DIRECT,
                avatarColorHex = fatmata.avatarColorHex,
                directUser = fatmata,
                unreadCount = 0
            ),
            ChatRoom(
                id = "chat_samuel",
                name = "Dr. Samuel Kamara",
                category = ChatCategory.DIRECT,
                avatarColorHex = samuel.avatarColorHex,
                directUser = samuel,
                unreadCount = 0
            )
        )
    }

    private fun createInitialMessages(chats: List<ChatRoom>, users: List<User>): Map<String, List<Message>> {
        val map = mutableMapOf<String, List<Message>>()

        // KrioGPT Bot Chat
        map["chat_kriogpt"] = listOf(
            Message(
                id = "msg_kg_1",
                chatId = "chat_kriogpt",
                senderId = "user_kriogpt",
                senderName = "KrioGPT AI",
                isOutgoing = false,
                text = "Kusheh! 🇸🇱 I am your Salon Na We Yon AI companion. You can ask me to summarize chats, translate messages to Krio/Mende/Temne, plan your itinerary in Freetown, or write business memos!",
                type = MessageType.TEXT,
                timeFormatted = "10:00 AM",
                isAiGenerated = true
            )
        )

        // Salone Tech Group Chat
        map["chat_group_salone_devs"] = listOf(
            Message(
                id = "msg_dev_1",
                chatId = "chat_group_salone_devs",
                senderId = "user_aminata",
                senderName = "Aminata Bangura",
                isOutgoing = false,
                text = "Kusheh everyone! The new Salon Na We Yon high-speed network update is live across Sierra Leone! 🚀",
                type = MessageType.TEXT,
                timeFormatted = "11:15 AM",
                reactions = listOf(MessageReaction(emoji = "🇸🇱", count = 6, userIds = listOf("user_me"), hasReacted = true), MessageReaction(emoji = "🔥", count = 4))
            ),
            Message(
                id = "msg_dev_2",
                chatId = "chat_group_salone_devs",
                senderId = "user_mohamed",
                senderName = "Mohamed Turay",
                isOutgoing = false,
                text = "Tested the voice call feature from Bo Town to Freetown, crystal clear HD audio! Here is our deployment summary doc.",
                type = MessageType.DOCUMENT,
                timeFormatted = "11:20 AM",
                documentData = DocumentPayload(fileName = "Salone_Tech_Architecture_2025.pdf", fileSizeText = "3.8 MB", fileExtension = "PDF")
            ),
            Message(
                id = "msg_dev_3",
                chatId = "chat_group_salone_devs",
                senderId = "user_me",
                senderName = "Joseph Conteh",
                isOutgoing = true,
                text = "Great job team! Let's pin the meetup location for this Saturday at Lumley Beach.",
                type = MessageType.TEXT,
                timeFormatted = "11:30 AM",
                isPinned = true
            ),
            Message(
                id = "msg_dev_4",
                chatId = "chat_group_salone_devs",
                senderId = "user_aminata",
                senderName = "Aminata Bangura",
                isOutgoing = false,
                text = "Here is the exact beach venue location:",
                type = MessageType.LOCATION,
                timeFormatted = "11:32 AM",
                locationData = LocationPayload(latitude = 8.4844, longitude = -13.2818, placeName = "Lumley Beach Boardwalk", address = "Lumley Beach Rd, Freetown, Sierra Leone")
            )
        )

        // Aminata Direct Chat
        map["chat_aminata"] = listOf(
            Message(
                id = "msg_am_1",
                chatId = "chat_aminata",
                senderId = "user_aminata",
                senderName = "Aminata Bangura",
                isOutgoing = false,
                text = "Joseph, aw di body? Are you free for a quick voice call regarding the new Cotton Tree project?",
                type = MessageType.TEXT,
                timeFormatted = "Yesterday, 2:10 PM"
            ),
            Message(
                id = "msg_am_2",
                chatId = "chat_aminata",
                senderId = "user_me",
                senderName = "Joseph Conteh",
                isOutgoing = true,
                text = "A dey wel! Sure, listen to my voice note update below:",
                type = MessageType.VOICE_NOTE,
                timeFormatted = "Yesterday, 2:15 PM",
                audioData = AudioPayload(durationSeconds = 24, waveformPoints = listOf(0.2f, 0.5f, 0.8f, 0.4f, 0.9f, 0.7f, 0.3f, 0.6f, 0.8f, 0.2f))
            ),
            Message(
                id = "msg_am_3",
                chatId = "chat_aminata",
                senderId = "user_aminata",
                senderName = "Aminata Bangura",
                isOutgoing = false,
                text = "Perfect! Let's finalize it tonight.",
                type = MessageType.TEXT,
                timeFormatted = "Yesterday, 2:20 PM",
                reactions = listOf(MessageReaction(emoji = "👍", count = 1, userIds = listOf("user_me"), hasReacted = true))
            )
        )

        // Marketplace Group Chat
        map["chat_group_marketplace"] = listOf(
            Message(
                id = "msg_mk_1",
                chatId = "chat_group_marketplace",
                senderId = "user_fatmata",
                senderName = "Fatmata Koroma",
                isOutgoing = false,
                text = "Brand new Sierra Leone athletic Ronko jerseys available! NLe 350 each. Fast delivery across Western Area.",
                type = MessageType.TEXT,
                timeFormatted = "09:45 AM"
            )
        )

        // Mohamed Direct Chat
        map["chat_mohamed"] = listOf(
            Message(
                id = "msg_mo_1",
                chatId = "chat_mohamed",
                senderId = "user_mohamed",
                senderName = "Mohamed Turay",
                isOutgoing = false,
                text = "Kusheh Joseph! Hope the Bo Town branch report was helpful.",
                type = MessageType.TEXT,
                timeFormatted = "Monday"
            )
        )

        return map
    }

    private fun createInitialStories(users: List<User>): List<StoryItem> {
        val me = _currentUser.value
        val aminata = users.find { it.id == "user_aminata" } ?: users[1]
        val mohamed = users.find { it.id == "user_mohamed" } ?: users[2]

        return listOf(
            StoryItem(
                id = "story_me",
                userId = me.id,
                userName = "My Status",
                userAvatar = me.avatarUrl,
                userAvatarColor = me.avatarColorHex,
                timestamp = System.currentTimeMillis() - 3600000,
                timeAgoFormatted = "1h ago",
                textContent = "Building the future of Sierra Leone communication on Salon Na We Yon! 🇸🇱💚🤍💙",
                backgroundGradientHex = listOf("#008751", "#0066B2"),
                isMine = true,
                isSeenByMe = true,
                viewers = listOf(
                    StoryViewer(user = aminata, viewedAt = "45m ago", reactionEmoji = "🔥"),
                    StoryViewer(user = mohamed, viewedAt = "30m ago", reactionEmoji = "🇸🇱")
                )
            ),
            StoryItem(
                id = "story_aminata",
                userId = aminata.id,
                userName = aminata.displayName,
                userAvatar = aminata.avatarUrl,
                userAvatarColor = aminata.avatarColorHex,
                timestamp = System.currentTimeMillis() - 7200000,
                timeAgoFormatted = "2h ago",
                textContent = "Sunset at Aberdeen Beach, Freetown. Sweet Salone! 🌅🌴",
                backgroundGradientHex = listOf("#FBB034", "#E11D48"),
                isMine = false,
                isSeenByMe = false,
                viewers = emptyList()
            ),
            StoryItem(
                id = "story_mohamed",
                userId = mohamed.id,
                userName = mohamed.displayName,
                userAvatar = mohamed.avatarUrl,
                userAvatarColor = mohamed.avatarColorHex,
                timestamp = System.currentTimeMillis() - 14400000,
                timeAgoFormatted = "4h ago",
                textContent = "Bo Town Tech Hub Innovation Demo Day was a massive success! 💡💻",
                backgroundGradientHex = listOf("#0066B2", "#008751"),
                isMine = false,
                isSeenByMe = false,
                viewers = emptyList()
            )
        )
    }

    private fun createInitialCalls(users: List<User>): List<CallRecord> {
        val aminata = users.find { it.id == "user_aminata" } ?: users[1]
        val mohamed = users.find { it.id == "user_mohamed" } ?: users[2]
        val fatmata = users.find { it.id == "user_fatmata" } ?: users[3]

        return listOf(
            CallRecord(
                id = "call_1",
                user = aminata,
                mediaType = CallMediaType.VIDEO,
                direction = CallDirection.INCOMING,
                timeFormatted = "Today, 10:30 AM",
                durationFormatted = "12m 45s"
            ),
            CallRecord(
                id = "call_2",
                user = mohamed,
                mediaType = CallMediaType.VOICE,
                direction = CallDirection.OUTGOING,
                timeFormatted = "Yesterday, 3:15 PM",
                durationFormatted = "5m 12s"
            ),
            CallRecord(
                id = "call_3",
                user = fatmata,
                mediaType = CallMediaType.VOICE,
                direction = CallDirection.MISSED,
                timeFormatted = "Sunday, 6:00 PM",
                durationFormatted = "Missed"
            ),
            CallRecord(
                id = "call_4",
                user = aminata,
                isGroupCall = true,
                groupName = "🇸🇱 Salone Tech Team",
                mediaType = CallMediaType.VIDEO,
                direction = CallDirection.INCOMING,
                timeFormatted = "Friday, 4:00 PM",
                durationFormatted = "45m 10s"
            )
        )
    }

    private fun createInitialReports(): List<ReportItem> {
        return listOf(
            ReportItem(
                id = "rep_1",
                reporterId = "user_aminata",
                reporterName = "Aminata Bangura",
                reportedUserId = "bad_user_99",
                reportedUserName = "SuspiciousSeller99",
                reportedMessageSnippet = "Send 500 Leones to this random unregistered number now for 10x reward",
                reason = "Spam / Fraudulent scam",
                timestamp = "20 mins ago",
                status = ReportStatus.PENDING
            ),
            ReportItem(
                id = "rep_2",
                reporterId = "user_samuel",
                reporterName = "Dr. Samuel Kamara",
                reportedUserId = "bad_user_102",
                reportedUserName = "TrollAccount",
                reportedMessageSnippet = "Spamming aggressive messages in group chat",
                reason = "Harassment / Abusive behavior",
                timestamp = "1 hour ago",
                status = ReportStatus.PENDING
            )
        )
    }

    private fun createInitialAnnouncements(): List<SystemAnnouncement> {
        return listOf(
            SystemAnnouncement(
                id = "ann_1",
                title = "🇸🇱 Salon Na We Yon v3.0 Live!",
                content = "Welcome to the new unified messaging experience with full Sierra Leone dialect support, end-to-end encryption, 4K video calling, and AI chat assistant.",
                timestamp = "Today, 08:00 AM",
                isPriority = true
            ),
            SystemAnnouncement(
                id = "ann_2",
                title = "⚡ Fiber Latency Optimization",
                content = "Freetown, Bo, Kenema and Makeni server routing improved by 40%. Messages and video streams now deliver under 20ms.",
                timestamp = "Yesterday",
                isPriority = false
            )
        )
    }
}
