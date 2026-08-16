package com.agon.app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val phoneNumber: String,
    val email: String,
    val avatarUrl: String? = null,
    val avatarColorHex: String = "#008751",
    val bio: String = "Proud Salone 🇸🇱 | Salon Na We Yon",
    val isOnline: Boolean = true,
    val lastSeenText: String = "Online",
    val isVerified: Boolean = false,
    val isVip: Boolean = false,
    val isBusiness: Boolean = false,
    val isBlocked: Boolean = false,
    val customStatus: String? = null,
    val tribeOrLocation: String = "Freetown, Sierra Leone"
)

@Serializable
enum class AccountTier {
    FREE,
    VIP_PLUS,
    BUSINESS_PRO
}

@Serializable
data class PrivacySettings(
    val lastSeenVisibility: String = "Everyone", // Everyone, My Contacts, Nobody
    val profilePhotoVisibility: String = "Everyone",
    val bioVisibility: String = "Everyone",
    val statusVisibility: String = "My Contacts",
    val readReceiptsEnabled: Boolean = true,
    val groupsWhoCanAddMe: String = "Everyone",
    val appLockEnabled: Boolean = false,
    val appLockPin: String = "",
    val biometricEnabled: Boolean = false,
    val twoFactorEnabled: Boolean = false,
    val twoFactorPin: String = "",
    val endToEndEncryptionEnabled: Boolean = true
)

@Serializable
data class NotificationPreferences(
    val messageNotifications: Boolean = true,
    val groupNotifications: Boolean = true,
    val callNotifications: Boolean = true,
    val mentionNotifications: Boolean = true,
    val showPreviews: Boolean = true,
    val soundName: String = "Salone Chime",
    val vibrate: Boolean = true,
    val inAppBanners: Boolean = true
)

@Serializable
data class ContactItem(
    val user: User,
    val isFavorite: Boolean = false,
    val savedName: String? = null,
    val mutualFriendsCount: Int = 12
)
