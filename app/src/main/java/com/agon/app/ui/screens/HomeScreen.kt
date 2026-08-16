package com.agon.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.ChatCategory
import com.agon.app.data.models.SaloneDictionary
import com.agon.app.data.models.User
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.components.InAppToastNotification
import com.agon.app.ui.components.MediaPreviewDialog
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold
import com.agon.app.viewmodel.MainViewModel

enum class MainTab {
    CHATS,
    CALLS,
    DISCOVER,
    STORIES,
    SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val activeChatId by viewModel.currentActiveChatId.collectAsState()
    val activeStory by viewModel.activeViewingStory.collectAsState()
    val previewMedia by viewModel.previewMedia.collectAsState()
    val forwardingMessage by viewModel.forwardingMessage.collectAsState()
    val selectedProfile by viewModel.selectedUserProfile.collectAsState()
    val showCreateGroupSheet by viewModel.showCreateGroupSheet.collectAsState()
    val showStoryCreator by viewModel.showStoryCreator.collectAsState()
    val showQrShareModal by viewModel.showQrShareModal.collectAsState()
    val activeCall by viewModel.activeCall.collectAsState()
    val inAppToast by viewModel.inAppNotification.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val chats by viewModel.chats.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    var activeTab by remember { mutableStateOf(MainTab.CHATS) }
    var currentSubScreen by remember { mutableStateOf<String?>(null) } // "security", "admin", "premium", "business", "ai", "search", "group_info"
    var groupInfoTargetId by remember { mutableStateOf<String?>(null) }
    var showAccountSwitchMenu by remember { mutableStateOf(false) }

    // If logged out, render AuthScreen
    if (!isLoggedIn) {
        AuthScreen(viewModel = viewModel)
        return
    }

    // If active call is ongoing, render CallScreen overlay
    if (activeCall != null) {
        CallScreen(viewModel = viewModel)
        return
    }

    // If viewing a story, render StoryViewerScreen
    if (activeStory != null) {
        StoryViewerScreen(
            story = activeStory!!,
            viewModel = viewModel,
            onClose = { viewModel.closeStory() }
        )
        return
    }

    // If creating a story, render StoryCreatorScreen
    if (showStoryCreator) {
        StoryCreatorScreen(
            viewModel = viewModel,
            onClose = { viewModel.setStoryCreatorVisible(false) }
        )
        return
    }

    // If QR Share Modal is opened
    if (showQrShareModal) {
        QRShareScreen(
            viewModel = viewModel,
            onClose = { viewModel.setQrShareModalVisible(false) }
        )
        return
    }

    // Sub-screens Navigation Handling
    when (currentSubScreen) {
        "security" -> {
            SecurityPrivacyScreen(viewModel = viewModel, onBack = { currentSubScreen = null })
            return
        }
        "admin" -> {
            AdminDashboardScreen(viewModel = viewModel, onBack = { currentSubScreen = null })
            return
        }
        "premium" -> {
            PremiumScreen(viewModel = viewModel, onBack = { currentSubScreen = null })
            return
        }
        "business" -> {
            BusinessHubScreen(viewModel = viewModel, onBack = { currentSubScreen = null })
            return
        }
        "ai" -> {
            AIAssistantScreen(viewModel = viewModel, onBack = { currentSubScreen = null })
            return
        }
        "search" -> {
            SearchScreen(viewModel = viewModel, onBack = { currentSubScreen = null })
            return
        }
        "group_info" -> {
            groupInfoTargetId?.let { gid ->
                GroupInfoScreen(chatId = gid, viewModel = viewModel, onBack = { currentSubScreen = null })
                return
            }
        }
    }

    // If a chat is actively open, render ChatDetailScreen
    if (activeChatId != null) {
        ChatDetailScreen(
            chatId = activeChatId!!,
            viewModel = viewModel,
            onBack = { viewModel.closeChat() },
            onOpenGroupInfo = { gid ->
                groupInfoTargetId = gid
                currentSubScreen = "group_info"
            }
        )
        return
    }

    // MAIN BOTTOM NAVIGATION SCAFFOLD
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SaloneEmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "salon na we yon",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = SaloneEmeraldPrimary
                                )
                                Text(
                                    text = "Sierra Leone Super Messenger 🇸🇱",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { currentSubScreen = "ai" }) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = SaloneEmeraldPrimary)
                        }
                        IconButton(onClick = { currentSubScreen = "search" }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { viewModel.setQrShareModalVisible(true) }) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = "QR Code")
                        }

                        // Switch Profile Button
                        Box {
                            IconButton(onClick = { showAccountSwitchMenu = true }) {
                                Icon(Icons.Default.SwitchAccount, contentDescription = "Switch Profile", tint = SaloneEmeraldPrimary)
                            }
                            DropdownMenu(
                                expanded = showAccountSwitchMenu,
                                onDismissRequest = { showAccountSwitchMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Active: ${viewModel.currentUser.value.displayName}", fontWeight = FontWeight.Bold, color = SaloneEmeraldPrimary) },
                                    onClick = {}
                                )
                                HorizontalDivider()
                                allUsers.forEach { user ->
                                    DropdownMenuItem(
                                        text = { Text("Switch to ${user.displayName} (@${user.username})") },
                                        leadingIcon = { AvatarView(name = user.displayName, size = 26.dp, isVerified = user.isVerified) },
                                        onClick = {
                                            viewModel.repository.switchAccount(user)
                                            showAccountSwitchMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val unreadTotal = chats.sumOf { it.unreadCount }

                    NavigationBarItem(
                        icon = {
                            BadgedBox(badge = {
                                if (unreadTotal > 0) {
                                    Badge { Text(unreadTotal.toString()) }
                                }
                            }) {
                                Icon(
                                    imageVector = if (activeTab == MainTab.CHATS) Icons.Default.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                                    contentDescription = "Chats"
                                )
                            }
                        },
                        label = { Text(SaloneDictionary.getChatsTabLabel(currentLang), fontSize = 10.sp) },
                        selected = activeTab == MainTab.CHATS,
                        onClick = { activeTab = MainTab.CHATS }
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (activeTab == MainTab.CALLS) Icons.Default.Call else Icons.Outlined.Call,
                                contentDescription = "Calls"
                            )
                        },
                        label = { Text(SaloneDictionary.getCallsTabLabel(currentLang), fontSize = 10.sp) },
                        selected = activeTab == MainTab.CALLS,
                        onClick = { activeTab = MainTab.CALLS }
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (activeTab == MainTab.DISCOVER) Icons.Default.Explore else Icons.Outlined.Explore,
                                contentDescription = "Discover"
                            )
                        },
                        label = { Text(SaloneDictionary.getDiscoverTabLabel(currentLang), fontSize = 10.sp) },
                        selected = activeTab == MainTab.DISCOVER,
                        onClick = { activeTab = MainTab.DISCOVER }
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (activeTab == MainTab.STORIES) Icons.Default.AutoAwesomeMotion else Icons.Outlined.AutoAwesomeMotion,
                                contentDescription = "Status"
                            )
                        },
                        label = { Text(SaloneDictionary.getStoriesTabLabel(currentLang), fontSize = 10.sp) },
                        selected = activeTab == MainTab.STORIES,
                        onClick = { activeTab = MainTab.STORIES }
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (activeTab == MainTab.SETTINGS) Icons.Default.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text(SaloneDictionary.getSettingsTabLabel(currentLang), fontSize = 10.sp) },
                        selected = activeTab == MainTab.SETTINGS,
                        onClick = { activeTab = MainTab.SETTINGS }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (activeTab) {
                    MainTab.CHATS -> ChatListTab(viewModel = viewModel)
                    MainTab.CALLS -> CallsTab(viewModel = viewModel)
                    MainTab.DISCOVER -> DiscoverTab(viewModel = viewModel)
                    MainTab.STORIES -> StoriesTab(viewModel = viewModel)
                    MainTab.SETTINGS -> SettingsScreen(
                        viewModel = viewModel,
                        onOpenSecurity = { currentSubScreen = "security" },
                        onOpenAdmin = { currentSubScreen = "admin" },
                        onOpenPremium = { currentSubScreen = "premium" },
                        onOpenBusiness = { currentSubScreen = "business" },
                        onOpenAiChat = { currentSubScreen = "ai" }
                    )
                }
            }
        }

        // In-App Toast Notification
        InAppToastNotification(
            message = inAppToast,
            onDismiss = { viewModel.clearNotificationToast() }
        )
    }

    // Media Preview Lightbox Dialog
    previewMedia?.let { msg ->
        MediaPreviewDialog(
            message = msg,
            onDismiss = { viewModel.closeMediaPreview() },
            onDownload = {
                viewModel.repository.clearInAppNotification()
                viewModel.closeMediaPreview()
            }
        )
    }

    // Create Group Sheet
    if (showCreateGroupSheet) {
        CreateGroupSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.setCreateGroupSheetVisible(false) },
            onGroupCreated = { newId ->
                viewModel.setCreateGroupSheetVisible(false)
                viewModel.openChat(newId)
            }
        )
    }

    // Forwarding Message Modal
    forwardingMessage?.let { (srcChatId, msgToForward) ->
        ForwardMessageModal(
            chats = chats,
            onDismiss = { viewModel.cancelForward() },
            onSelectTargetChat = { targetChatId ->
                viewModel.completeForwardToChat(targetChatId)
            }
        )
    }

    // User Profile Sheet
    selectedProfile?.let { u ->
        UserProfileModal(
            user = u,
            onDismiss = { viewModel.closeUserProfile() },
            onDirectChat = {
                val cid = viewModel.repository.getOrCreateDirectChat(u)
                viewModel.closeUserProfile()
                viewModel.openChat(cid)
            },
            onVoiceCall = {
                viewModel.closeUserProfile()
                viewModel.repository.startCall(u, com.agon.app.data.models.CallMediaType.VOICE)
            },
            onVideoCall = {
                viewModel.closeUserProfile()
                viewModel.repository.startCall(u, com.agon.app.data.models.CallMediaType.VIDEO)
            },
            onBlock = {
                viewModel.repository.blockUser(u.id)
                viewModel.closeUserProfile()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onGroupCreated: (String) -> Unit
) {
    val allUsers by viewModel.allUsers.collectAsState()
    var groupName by remember { mutableStateOf("") }
    var groupDesc by remember { mutableStateOf("") }
    var isAnnouncementsOnly by remember { mutableStateOf(false) }
    val selectedUserIds = remember { mutableStateListOf<String>() }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text("Create Salone Group 🇸🇱", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name (e.g. Cotton Tree Innovators)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = groupDesc,
                onValueChange = { groupDesc = it },
                label = { Text("Group Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isAnnouncementsOnly, onCheckedChange = { isAnnouncementsOnly = it })
                Spacer(modifier = Modifier.width(6.dp))
                Text("Channel Mode (Only admins can post announcements)", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Select Members (${selectedUserIds.size}):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(modifier = Modifier.height(160.dp)) {
                items(allUsers) { user ->
                    val isSelected = selectedUserIds.contains(user.id)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isSelected) selectedUserIds.remove(user.id) else selectedUserIds.add(user.id)
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(checked = isSelected, onCheckedChange = {
                            if (it) selectedUserIds.add(user.id) else selectedUserIds.remove(user.id)
                        })
                        Spacer(modifier = Modifier.width(8.dp))
                        AvatarView(name = user.displayName, size = 32.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(user.displayName, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text(user.tribeOrLocation, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (groupName.isNotBlank()) {
                        val newGid = viewModel.repository.createGroup(
                            name = groupName,
                            description = groupDesc,
                            selectedUserIds = selectedUserIds.toList(),
                            isAnnouncementsOnly = isAnnouncementsOnly
                        )
                        onGroupCreated(newGid)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Group Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardMessageModal(
    chats: List<com.agon.app.data.models.ChatRoom>,
    onDismiss: () -> Unit,
    onSelectTargetChat: (String) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text("Forward Message To:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                items(chats) { room ->
                    ListItem(
                        headlineContent = { Text(room.name, fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text(if (room.category == ChatCategory.GROUP) "Group 🇸🇱" else "Direct Chat", fontSize = 11.sp) },
                        leadingContent = { AvatarView(name = room.name, size = 38.dp) },
                        modifier = Modifier.clickable {
                            onSelectTargetChat(room.id)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileModal(
    user: User,
    onDismiss: () -> Unit,
    onDirectChat: () -> Unit,
    onVoiceCall: () -> Unit,
    onVideoCall: () -> Unit,
    onBlock: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AvatarView(
                name = user.displayName,
                avatarUrl = user.avatarUrl,
                colorHex = user.avatarColorHex,
                size = 72.dp,
                isVerified = user.isVerified,
                isVip = user.isVip
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("@${user.username}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(user.tribeOrLocation, fontSize = 12.sp, color = SaloneEmeraldPrimary, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = user.bio,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = onDirectChat,
                    colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chat")
                }
                OutlinedButton(
                    onClick = onVoiceCall,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call")
                }
                OutlinedButton(
                    onClick = onVideoCall,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Video")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onBlock,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Block Contact")
            }
        }
    }
}
