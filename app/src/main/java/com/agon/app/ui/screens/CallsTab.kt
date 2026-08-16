package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.*
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsTab(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val callHistory by viewModel.callHistory.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") } // All, Missed
    var showNewCallDialog by remember { mutableStateOf(false) }

    val filteredCalls = remember(callHistory, selectedFilter) {
        if (selectedFilter == "Missed") {
            callHistory.filter { it.direction == CallDirection.MISSED }
        } else {
            callHistory
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Filter Tabs & Simulate Call Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedFilter == "All",
                            onClick = { selectedFilter = "All" },
                            label = { Text("All Calls") }
                        )
                        FilterChip(
                            selected = selectedFilter == "Missed",
                            onClick = { selectedFilter = "Missed" },
                            label = { Text("Missed") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }

                    // Test Incoming Call Simulator
                    TextButton(
                        onClick = {
                            val caller = allUsers.firstOrNull { it.id == "user_aminata" } ?: allUsers.first()
                            viewModel.repository.simulateIncomingCall(caller, CallMediaType.VIDEO)
                        }
                    ) {
                        Icon(Icons.Default.RingVolume, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Simulate Call", fontSize = 11.sp)
                    }
                }
            }

            // Call items
            if (filteredCalls.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.PhoneMissed, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No call history yet", fontWeight = FontWeight.SemiBold)
                        Text("Make crystal clear HD voice & video calls across Salone!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(filteredCalls, key = { it.id }) { call ->
                    CallHistoryRow(
                        call = call,
                        onCallBackVoice = {
                            viewModel.repository.startCall(call.user, CallMediaType.VOICE, call.isGroupCall, call.groupName)
                        },
                        onCallBackVideo = {
                            viewModel.repository.startCall(call.user, CallMediaType.VIDEO, call.isGroupCall, call.groupName)
                        }
                    )
                }
            }
        }

        // Start New Call FAB
        FloatingActionButton(
            onClick = { showNewCallDialog = true },
            containerColor = SaloneEmeraldPrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 86.dp, end = 16.dp)
        ) {
            Icon(Icons.Default.Call, contentDescription = "New Call")
        }
    }

    // New Call Modal
    if (showNewCallDialog) {
        AlertDialog(
            onDismissRequest = { showNewCallDialog = false },
            title = { Text("Start Call with Contact") },
            text = {
                LazyColumn(modifier = Modifier.height(240.dp)) {
                    items(allUsers) { user ->
                        ListItem(
                            headlineContent = { Text(user.displayName) },
                            supportingContent = { Text(user.tribeOrLocation, fontSize = 11.sp) },
                            leadingContent = { AvatarView(name = user.displayName, size = 36.dp) },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = {
                                        showNewCallDialog = false
                                        viewModel.repository.startCall(user, CallMediaType.VOICE)
                                    }) {
                                        Icon(Icons.Default.Call, contentDescription = "Voice", tint = SaloneEmeraldPrimary)
                                    }
                                    IconButton(onClick = {
                                        showNewCallDialog = false
                                        viewModel.repository.startCall(user, CallMediaType.VIDEO)
                                    }) {
                                        Icon(Icons.Default.Videocam, contentDescription = "Video", tint = SaloneEmeraldPrimary)
                                    }
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNewCallDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CallHistoryRow(
    call: CallRecord,
    onCallBackVoice: () -> Unit,
    onCallBackVideo: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarView(
                name = if (call.isGroupCall) (call.groupName ?: "Group") else call.user.displayName,
                avatarUrl = call.user.avatarUrl,
                colorHex = call.user.avatarColorHex,
                size = 46.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (call.isGroupCall) (call.groupName ?: "Salone Group") else call.user.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val directionIcon = when (call.direction) {
                        CallDirection.INCOMING -> Icons.Default.CallReceived
                        CallDirection.OUTGOING -> Icons.Default.CallMade
                        CallDirection.MISSED -> Icons.Default.CallMissed
                    }
                    val directionColor = when (call.direction) {
                        CallDirection.INCOMING -> SaloneEmeraldPrimary
                        CallDirection.OUTGOING -> MaterialTheme.colorScheme.primary
                        CallDirection.MISSED -> MaterialTheme.colorScheme.error
                    }
                    Icon(
                        imageVector = directionIcon,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = directionColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${call.timeFormatted} (${call.durationFormatted})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onCallBackVoice) {
                    Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = SaloneEmeraldPrimary)
                }
                IconButton(onClick = onCallBackVideo) {
                    Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = SaloneEmeraldPrimary)
                }
            }
        }
    }
}
