package com.agon.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.MemberRole
import com.agon.app.data.models.User
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold
import com.agon.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupInfoScreen(
    chatId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val chats by viewModel.chats.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val chat = chats.find { it.id == chatId }

    var isEditingInfo by remember { mutableStateOf(false) }
    var groupName by remember(chat) { mutableStateOf(chat?.name ?: "") }
    var groupDesc by remember(chat) { mutableStateOf(chat?.groupDescription ?: "") }
    var groupRules by remember(chat) { mutableStateOf(chat?.groupRules ?: "") }
    var announcementsOnly by remember(chat) { mutableStateOf(chat?.isAnnouncementsOnly ?: false) }

    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showSlowModeDialog by remember { mutableStateOf(false) }

    if (chat == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Group not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group Info", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditingInfo) {
                        IconButton(onClick = {
                            viewModel.repository.updateGroupInfo(chatId, groupName, groupDesc, groupRules, announcementsOnly)
                            isEditingInfo = false
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Save", tint = SaloneEmeraldPrimary)
                        }
                    } else {
                        IconButton(onClick = { isEditingInfo = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header: Group Avatar & Title
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AvatarView(
                        name = chat.name,
                        avatarUrl = chat.avatarUrl,
                        colorHex = chat.avatarColorHex,
                        size = 80.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (isEditingInfo) {
                        OutlinedTextField(
                            value = groupName,
                            onValueChange = { groupName = it },
                            label = { Text("Group Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = groupDesc,
                            onValueChange = { groupDesc = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    } else {
                        Text(chat.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${chat.members.size} Members • Group 🇸🇱",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!chat.groupDescription.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = chat.groupDescription,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                HorizontalDivider()
            }

            // Group Controls (Invite Link, Slow Mode, Announcements Only)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        ListItem(
                            headlineContent = { Text("Invite via Link / QR Code") },
                            supportingContent = { Text("https://salonnaweyon.sl/j/${chat.inviteCode}", fontSize = 11.sp) },
                            leadingContent = { Icon(Icons.Default.Link, contentDescription = null, tint = SaloneEmeraldPrimary) },
                            trailingContent = {
                                IconButton(onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Group Link", "https://salonnaweyon.sl/j/${chat.inviteCode}")
                                    clipboard.setPrimaryClip(clip)
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                }
                            }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        ListItem(
                            headlineContent = { Text("Slow Mode") },
                            supportingContent = { Text(if (chat.permissions.slowModeSeconds > 0) "${chat.permissions.slowModeSeconds} seconds between messages" else "Disabled") },
                            leadingContent = { Icon(Icons.Default.Timer, contentDescription = null) },
                            modifier = Modifier.clickable { showSlowModeDialog = true }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        ListItem(
                            headlineContent = { Text("Announcements Only") },
                            supportingContent = { Text("Only admins can send messages") },
                            leadingContent = { Icon(Icons.Default.Campaign, contentDescription = null) },
                            trailingContent = {
                                Switch(
                                    checked = announcementsOnly,
                                    onCheckedChange = {
                                        announcementsOnly = it
                                        viewModel.repository.updateGroupInfo(chatId, groupName, groupDesc, groupRules, it)
                                    }
                                )
                            }
                        )
                    }
                }
            }

            // Group Rules
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = SaloneEmeraldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Group Rules & Guidelines 🇸🇱", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(chat.groupRules ?: "No spam, be respectful to all Sierra Leoneans.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Members Header & Add Member Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${chat.members.size} Members", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Button(
                        onClick = { showAddMemberDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Member", fontSize = 12.sp)
                    }
                }
            }

            // Members List
            items(chat.members) { member ->
                ListItem(
                    headlineContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(member.user.displayName, fontWeight = FontWeight.SemiBold)
                            if (member.role == MemberRole.ADMIN || member.role == MemberRole.OWNER) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (member.role == MemberRole.OWNER) SaloneGold else SaloneEmeraldPrimary
                                ) {
                                    Text(
                                        text = member.role.name,
                                        fontSize = 9.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    },
                    supportingContent = { Text(member.user.tribeOrLocation, fontSize = 11.sp) },
                    leadingContent = {
                        AvatarView(name = member.user.displayName, size = 40.dp)
                    },
                    trailingContent = {
                        if (member.role != MemberRole.OWNER) {
                            var showMemberMenu by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { showMemberMenu = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Member options")
                                }
                                DropdownMenu(
                                    expanded = showMemberMenu,
                                    onDismissRequest = { showMemberMenu = false }
                                ) {
                                    if (member.role == MemberRole.MEMBER) {
                                        DropdownMenuItem(
                                            text = { Text("Make Group Admin") },
                                            onClick = {
                                                showMemberMenu = false
                                                viewModel.repository.updateMemberRole(chatId, member.user.id, MemberRole.ADMIN)
                                            }
                                        )
                                    } else {
                                        DropdownMenuItem(
                                            text = { Text("Dismiss as Admin") },
                                            onClick = {
                                                showMemberMenu = false
                                                viewModel.repository.updateMemberRole(chatId, member.user.id, MemberRole.MEMBER)
                                            }
                                        )
                                    }
                                    DropdownMenuItem(
                                        text = { Text("Remove from Group", color = MaterialTheme.colorScheme.error) },
                                        onClick = {
                                            showMemberMenu = false
                                            viewModel.repository.removeGroupMember(chatId, member.user.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }

            // Leave / Delete Group Danger Zone
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = {
                                viewModel.repository.deleteGroup(chatId)
                                onBack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Leave & Delete Group", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Add Member Dialog
    if (showAddMemberDialog) {
        val availableUsers = allUsers.filter { u -> chat.members.none { it.user.id == u.id } }
        AlertDialog(
            onDismissRequest = { showAddMemberDialog = false },
            title = { Text("Add Members from Contacts") },
            text = {
                if (availableUsers.isEmpty()) {
                    Text("All your contacts are already in this group.")
                } else {
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(availableUsers) { user ->
                            ListItem(
                                headlineContent = { Text(user.displayName) },
                                supportingContent = { Text(user.tribeOrLocation, fontSize = 11.sp) },
                                leadingContent = { AvatarView(name = user.displayName, size = 32.dp) },
                                modifier = Modifier.clickable {
                                    viewModel.repository.addGroupMember(chatId, user)
                                    showAddMemberDialog = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddMemberDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Slow Mode Dialog
    if (showSlowModeDialog) {
        AlertDialog(
            onDismissRequest = { showSlowModeDialog = false },
            title = { Text("Set Slow Mode Delay") },
            text = {
                Column {
                    listOf(0 to "Off", 10 to "10 seconds", 30 to "30 seconds", 60 to "1 minute", 300 to "5 minutes").forEach { (sec, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.repository.toggleSlowMode(chatId, sec)
                                    showSlowModeDialog = false
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = chat.permissions.slowModeSeconds == sec,
                                onClick = {
                                    viewModel.repository.toggleSlowMode(chatId, sec)
                                    showSlowModeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSlowModeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
