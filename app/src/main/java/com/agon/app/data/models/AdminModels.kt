package com.agon.app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SystemAnalytics(
    val totalUsers: Int = 248520,
    val activeUsersToday: Int = 184310,
    val newRegistrationsToday: Int = 1420,
    val onlineUsersNow: Int = 42190,
    val totalMessagesSent: String = "12.4M",
    val totalGroupsCreated: Int = 8940,
    val pendingReportsCount: Int = 14,
    val bannedAccountsCount: Int = 312,
    val websocketLatencyMs: Int = 14,
    val serverUptimePercent: Double = 99.98
)

@Serializable
enum class ReportStatus {
    PENDING,
    INVESTIGATING,
    RESOLVED_WARNED,
    RESOLVED_BANNED,
    DISMISSED
}

@Serializable
data class ReportItem(
    val id: String,
    val reporterId: String,
    val reporterName: String,
    val reportedUserId: String,
    val reportedUserName: String,
    val reportedMessageSnippet: String,
    val reason: String, // "Spam / Fraud", "Hate speech / Harassment", "Inappropriate media", "Impersonation"
    val timestamp: String = "15 mins ago",
    val status: ReportStatus = ReportStatus.PENDING
)

@Serializable
data class SystemAnnouncement(
    val id: String,
    val title: String,
    val content: String,
    val author: String = "Salon Na We Yon HQ",
    val timestamp: String = "Today, 09:00 AM",
    val isPriority: Boolean = true
)
