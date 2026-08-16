package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.ReportStatus
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.components.SaloneSearchField
import com.agon.app.ui.theme.SaloneAtlanticBlue
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold
import com.agon.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val analytics by viewModel.adminAnalytics.collectAsState()
    val reports by viewModel.adminReports.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val announcements by viewModel.systemAnnouncements.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview, 1: Users, 2: Reports, 3: Announcements
    var userSearchQuery by remember { mutableStateOf("") }

    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastContent by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Command Center 🇸🇱", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showBroadcastDialog = true }) {
                        Icon(Icons.Default.Campaign, contentDescription = "Broadcast", tint = SaloneEmeraldPrimary)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = SaloneEmeraldPrimary
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("KPIs", fontSize = 12.sp) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Users", fontSize = 12.sp) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Reports (${reports.size})", fontSize = 12.sp) })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Alerts", fontSize = 12.sp) })
            }

            when (selectedTab) {
                0 -> {
                    // OVERVIEW & METRICS
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            // Server Health Indicator
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SaloneEmeraldPrimary.copy(alpha = 0.12f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF10B981)))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Freetown High-Speed Cluster: Healthy", fontWeight = FontWeight.Bold, color = SaloneEmeraldPrimary)
                                        Text("WebSocket Latency: ${analytics.websocketLatencyMs}ms • Uptime: ${analytics.serverUptimePercent}%", fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                AdminKpiCard("Total Accounts", analytics.totalUsers.toString(), "🇸🇱 Sierra Leone", SaloneEmeraldPrimary, Modifier.weight(1f))
                                AdminKpiCard("Active Today", analytics.activeUsersToday.toString(), "+1,420 new reg", SaloneAtlanticBlue, Modifier.weight(1f))
                            }
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                AdminKpiCard("Online Right Now", analytics.onlineUsersNow.toString(), "Real-time sockets", Color(0xFF10B981), Modifier.weight(1f))
                                AdminKpiCard("Total Messages", analytics.totalMessagesSent, "Encrypted packets", SaloneGold, Modifier.weight(1f))
                            }
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                AdminKpiCard("Public Groups", analytics.totalGroupsCreated.toString(), "Active communities", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                                AdminKpiCard("Banned Accounts", analytics.bannedAccountsCount.toString(), "Spam protection", MaterialTheme.colorScheme.error, Modifier.weight(1f))
                            }
                        }
                    }
                }

                1 -> {
                    // USER MANAGEMENT
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            SaloneSearchField(
                                query = userSearchQuery,
                                onQueryChange = { userSearchQuery = it },
                                placeholder = "Search user by name, handle, phone..."
                            )
                        }

                        val filteredUsers = allUsers.filter {
                            it.displayName.contains(userSearchQuery, ignoreCase = true) ||
                                    it.username.contains(userSearchQuery, ignoreCase = true)
                        }

                        items(filteredUsers) { user ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AvatarView(name = user.displayName, size = 42.dp, isVerified = user.isVerified)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("@${user.username} • ${user.phoneNumber}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        if (user.isBlocked) {
                                            Text("STATUS: BANNED ⚠️", fontSize = 10.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(onClick = { viewModel.repository.adminToggleVerifyBadge(user.id) }) {
                                            Icon(
                                                imageVector = if (user.isVerified) Icons.Default.CheckCircle else Icons.Default.Verified,
                                                contentDescription = "Verify",
                                                tint = if (user.isVerified) SaloneEmeraldPrimary else Color.LightGray
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                if (user.isBlocked) {
                                                    viewModel.repository.adminUnbanUser(user.id)
                                                } else {
                                                    viewModel.repository.adminBanUser(user.id)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (user.isBlocked) SaloneEmeraldPrimary else MaterialTheme.colorScheme.error
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(if (user.isBlocked) "Unban" else "Ban", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // REPORTS MODERATION QUEUE
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(reports) { report ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Reported: ${report.reportedUserName}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.errorContainer
                                        ) {
                                            Text(
                                                text = report.status.name,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Reason: ${report.reason}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    Text("Message flagged: \"${report.reportedMessageSnippet}\"", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Reported by ${report.reporterName} • ${report.timestamp}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { viewModel.repository.adminResolveReport(report.id, ReportStatus.RESOLVED_BANNED) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Ban User", fontSize = 11.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.repository.adminResolveReport(report.id, ReportStatus.RESOLVED_WARNED) },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Send Warning", fontSize = 11.sp)
                                        }
                                        TextButton(
                                            onClick = { viewModel.repository.adminResolveReport(report.id, ReportStatus.DISMISSED) },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Dismiss", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // ANNOUNCEMENTS
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Button(
                                onClick = { showBroadcastDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary)
                            ) {
                                Icon(Icons.Default.Campaign, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Broadcast System Announcement 📢")
                            }
                        }

                        items(announcements) { ann ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(ann.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(ann.content, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("${ann.author} • ${ann.timestamp}", fontSize = 10.sp, color = SaloneEmeraldPrimary, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Broadcast Announcement Dialog
    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Broadcast System Alert 📢") },
            text = {
                Column {
                    Text("This announcement will be dispatched to all 248,000+ registered Sierra Leone users.", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text("Announcement Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = broadcastContent,
                        onValueChange = { broadcastContent = it },
                        label = { Text("Message Content") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (broadcastTitle.isNotBlank() && broadcastContent.isNotBlank()) {
                            viewModel.repository.adminBroadcastAnnouncement(broadcastTitle, broadcastContent)
                            showBroadcastDialog = false
                            broadcastTitle = ""
                            broadcastContent = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary)
                ) {
                    Text("Send Broadcast")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminKpiCard(
    title: String,
    value: String,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = accentColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtext, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
