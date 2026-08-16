package com.agon.app.data.models

import kotlinx.serialization.Serializable

@Serializable
enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    VOICE_NOTE,
    DOCUMENT,
    LOCATION,
    CONTACT_CARD,
    STICKER,
    GIF,
    SYSTEM_NOTICE
}

@Serializable
enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

@Serializable
data class MessageReaction(
    val emoji: String,
    val count: Int,
    val userIds: List<String> = emptyList(),
    val hasReacted: Boolean = false
)

@Serializable
data class LocationPayload(
    val latitude: Double,
    val longitude: Double,
    val placeName: String,
    val address: String
)

@Serializable
data class ContactPayload(
    val name: String,
    val phone: String,
    val organization: String? = null
)

@Serializable
data class DocumentPayload(
    val fileName: String,
    val fileSizeText: String,
    val fileExtension: String,
    val localUri: String? = null
)

@Serializable
data class AudioPayload(
    val durationSeconds: Int,
    val waveformPoints: List<Float> = emptyList(),
    val isVoiceNote: Boolean = true
)

@Serializable
data class ReplyReference(
    val messageId: String,
    val senderName: String,
    val snippetText: String,
    val mediaType: MessageType? = null
)

@Serializable
data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String? = null,
    val isOutgoing: Boolean,
    val text: String,
    val type: MessageType = MessageType.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String = "12:00 PM",
    val status: MessageStatus = MessageStatus.READ,
    val mediaUrl: String? = null,
    val localMediaPath: String? = null,
    val mediaCaption: String? = null,
    val isStarred: Boolean = false,
    val isPinned: Boolean = false,
    val isEdited: Boolean = false,
    val replyTo: ReplyReference? = null,
    val forwardFrom: String? = null,
    val reactions: List<MessageReaction> = emptyList(),
    val locationData: LocationPayload? = null,
    val contactData: ContactPayload? = null,
    val documentData: DocumentPayload? = null,
    val audioData: AudioPayload? = null,
    val isAiGenerated: Boolean = false,
    val translatedText: String? = null,
    val isSpamOrToxic: Boolean = false
)

@Serializable
enum class ChatCategory {
    DIRECT,
    GROUP,
    CHANNEL,
    AI_BOT,
    BUSINESS_SUPPORT
}

@Serializable
enum class MemberRole {
    OWNER,
    ADMIN,
    MODERATOR,
    MEMBER
}

@Serializable
data class GroupMember(
    val user: User,
    val role: MemberRole = MemberRole.MEMBER,
    val joinedAt: Long = System.currentTimeMillis(),
    val canSendMessages: Boolean = true,
    val canSendMedia: Boolean = true
)

@Serializable
data class GroupPermissions(
    val allowMembersToSendMessages: Boolean = true,
    val allowMembersToSendMedia: Boolean = true,
    val allowMembersToAddUsers: Boolean = true,
    val allowMembersToPinMessages: Boolean = false,
    val allowMembersToChangeInfo: Boolean = false,
    val requireAdminApprovalToJoin: Boolean = false,
    val slowModeSeconds: Int = 0 // 0 = off, 10, 30, 60, 300
)

@Serializable
data class ChatRoom(
    val id: String,
    val name: String,
    val category: ChatCategory = ChatCategory.DIRECT,
    val avatarUrl: String? = null,
    val avatarColorHex: String = "#008751",
    val directUser: User? = null,
    val groupDescription: String? = null,
    val groupRules: String? = "1. Respect all Sierra Leoneans & global members\n2. No hate speech or spam\n3. Use appropriate media tags\n4. Enjoy Salone hospitality!",
    val members: List<GroupMember> = emptyList(),
    val permissions: GroupPermissions = GroupPermissions(),
    val isMuted: Boolean = false,
    val muteUntil: String? = null,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val unreadCount: Int = 0,
    val lastMessage: Message? = null,
    val isTyping: Boolean = false,
    val typingUserName: String? = null,
    val pinnedMessage: Message? = null,
    val customWallpaperTheme: String = "default",
    val inviteCode: String = "SALONE-JOIN-2025",
    val isAnnouncementsOnly: Boolean = false,
    val isVerifiedBadge: Boolean = false
)

// Status / Story Models
@Serializable
data class StoryItem(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String? = null,
    val userAvatarColor: String = "#008751",
    val timestamp: Long = System.currentTimeMillis(),
    val timeAgoFormatted: String = "2h ago",
    val mediaUrl: String? = null,
    val isVideo: Boolean = false,
    val textContent: String = "",
    val backgroundGradientHex: List<String> = listOf("#008751", "#0066B2"),
    val fontStyle: String = "Bold",
    val caption: String? = null,
    val viewers: List<StoryViewer> = emptyList(),
    val isSeenByMe: Boolean = false,
    val isMine: Boolean = false
)

@Serializable
data class StoryViewer(
    val user: User,
    val viewedAt: String = "Just now",
    val reactionEmoji: String? = null
)

// Call Models
@Serializable
enum class CallDirection {
    INCOMING,
    OUTGOING,
    MISSED
}

@Serializable
enum class CallMediaType {
    VOICE,
    VIDEO
}

@Serializable
data class CallRecord(
    val id: String,
    val user: User,
    val isGroupCall: Boolean = false,
    val groupName: String? = null,
    val mediaType: CallMediaType = CallMediaType.VOICE,
    val direction: CallDirection = CallDirection.INCOMING,
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String = "Yesterday, 4:15 PM",
    val durationFormatted: String = "4m 32s"
)

@Serializable
data class ActiveCallState(
    val callId: String,
    val peerUser: User,
    val isGroupCall: Boolean = false,
    val groupName: String? = null,
    val participants: List<User> = emptyList(),
    val mediaType: CallMediaType = CallMediaType.VOICE,
    val isMuted: Boolean = false,
    val isCameraOff: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val isFrontCamera: Boolean = true,
    val isScreenSharing: Boolean = false,
    val durationSeconds: Int = 0,
    val connectionQuality: String = "HD • 5G (Excellent)",
    val isRinging: Boolean = false
)
