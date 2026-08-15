package com.agon.app.data

import kotlinx.serialization.Serializable

const val APP_MARK = "SNWY1"
const val APP_CHANNEL = "snwy/v1"
const val STATUS_TTL_MS = 24L * 60L * 60L * 1000L

@Serializable
data class Envelope(
    val app: String = APP_MARK,
    val kind: String,
    val body: String,
    val sentAt: Long = System.currentTimeMillis(),
)

@Serializable
data class UserProfile(
    val id: String,
    val handle: String,
    val displayName: String,
    val phone: String = "",
    val email: String = "",
    val city: String = "",
    val district: String = "",
    val region: String = "",
    val tribe: String = "",
    val language: String = "en",
    val bio: String = "",
    val photoUrl: String = "",
    val favoriteDish: String = "",
    val purpose: String = "",
    val passwordHash: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastSeen: Long = System.currentTimeMillis(),
    val online: Boolean = false,
    val theme: String = "LEONE_FLAG",
)

@Serializable
data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val recipientId: String = "",
    val text: String = "",
    val mediaUrl: String = "",
    val mediaType: String = "",
    val durationMs: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val delivered: Boolean = true,
)

@Serializable
data class StatusUpdate(
    val id: String,
    val userId: String,
    val userName: String,
    val userPhoto: String = "",
    val text: String = "",
    val mediaUrl: String = "",
    val mediaType: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

@Serializable
data class VoiceRoom(
    val id: String,
    val title: String,
    val hostId: String,
    val hostName: String,
    val topic: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

@Serializable
data class VoiceClip(
    val id: String,
    val roomId: String,
    val senderId: String,
    val senderName: String,
    val mediaUrl: String,
    val durationMs: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
)

@Serializable
data class PresencePing(
    val id: String,
    val name: String,
    val photoUrl: String = "",
    val city: String = "",
    val language: String = "en",
    val online: Boolean = true,
    val lastSeen: Long = System.currentTimeMillis(),
)

@Serializable
data class OnboardingAnswers(
    val displayName: String = "",
    val region: String = "",
    val language: String = "en",
    val favoriteDish: String = "",
    val purpose: String = "",
)

@Serializable
data class AiTurn(
    val id: String,
    val fromUser: Boolean,
    val text: String,
    val createdAt: Long = System.currentTimeMillis(),
)

@Serializable
data class AppSettings(
    val haptic: Boolean = true,
    val language: String = "en",
    val palette: String = "LEONE_FLAG",
    val darkMode: String = "system",
    val autoplayVoice: Boolean = false,
    val notifyMessages: Boolean = true,
    val onboardingDone: Boolean = false,
    val registered: Boolean = false,
)

data class ConversationPreview(
    val conversationId: String,
    val peerId: String,
    val peerName: String,
    val peerPhoto: String,
    val lastText: String,
    val lastAt: Long,
    val unread: Int,
    val online: Boolean,
)

enum class AppLang(val code: String, val nativeName: String, val englishName: String) {
    EN("en", "English", "English"),
    KRI("kri", "Krio", "Krio"),
    MEN("men", "Mɛnde yia", "Mende"),
    TEM("tem", "Temne", "Temne"),
    LMA("lma", "Limba", "Limba"),
    KNO("kno", "Kono", "Kono"),
    KSS("kss", "Kissi", "Kissi"),
    KNR("knr", "Kuranko", "Kuranko"),
    BUN("bun", "Sherbro / Bullom", "Sherbro"),
    FUL("ful", "Pular / Fula", "Fula"),
    MAN("man", "Mandingo", "Mandingo"),
    LOK("lok", "Loko", "Loko"),
}

fun appLangOf(code: String): AppLang =
    AppLang.entries.firstOrNull { it.code == code } ?: AppLang.EN

val sierraRegions = listOf(
    "Western Area Urban (Freetown)",
    "Western Area Rural",
    "Northern Province",
    "North West Province",
    "Eastern Province",
    "Southern Province",
    "Bo District",
    "Kenema District",
    "Bombali District",
    "Kailahun District",
    "Koinadugu District",
    "Kono District",
    "Moyamba District",
    "Port Loko District",
    "Pujehun District",
    "Tonkolili District",
    "Bonthe District",
    "Kambia District",
    "Karene District",
    "Falaba District",
    "Diaspora — live abroad",
)

val sierraTribes = listOf(
    "Mende", "Temne", "Limba", "Kono", "Koranko", "Fullah", "Mandingo",
    "Krio", "Kissi", "Loko", "Sherbro", "Susu", "Yalunka", "Vai",
    "Krim", "Gola", "Fula", "Lebanese-Sierra Leonean", "Other / Mixed",
)

val favoriteDishes = listOf(
    "Cassava leaf (plasas)",
    "Groundnut stew",
    "Potato leaf",
    "Crain crain",
    "Okra soup",
    "Jollof rice",
    "Fufu and plasas",
    "Fried fish and agidi",
    "Pepper soup",
    "Bean akara",
    "Roast cassava",
    "Other home food",
)

fun conversationIdFor(a: String, b: String): String =
    listOf(a, b).sorted().joinToString("_")

fun peerIdOf(conversationId: String, meId: String): String =
    conversationId.split("_").firstOrNull { it != meId } ?: conversationId
