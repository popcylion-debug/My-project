package com.agon.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.*
import com.agon.app.ui.components.*
import com.agon.app.ui.theme.*
import com.agon.app.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenGroupInfo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val chats by viewModel.chats.collectAsState()
    val allMessages by viewModel.messages.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    val chat = chats.find { it.id == chatId }
    val messages = allMessages[chatId] ?: emptyList()

    val listState = rememberLazyListState()

    // UI Input states
    var textInput by remember { mutableStateOf("") }
    var replyingToMessage by remember { mutableStateOf<Message?>(null) }
    var selectedActionMessage by remember { mutableStateOf<Message?>(null) }
    var editingMessage by remember { mutableStateOf<Message?>(null) }

    var showAttachmentsSheet by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showChatSearch by remember { mutableStateOf(false) }
    var inChatSearchQuery by remember { mutableStateOf("") }

    var showAiSummaryDialog by remember { mutableStateOf(false) }
    var aiSummaryText by remember { mutableStateOf("") }
    var showMediaGallerySheet by remember { mutableStateOf(false) }

    var showReportDialog by remember { mutableStateOf(false) }
    var reportTargetMsg by remember { mutableStateOf<Message?>(null) }
    var reportReason by remember { mutableStateOf("Spam or Fraud") }

    var showOverflowMenu by remember { mutableStateOf(false) }

    // Voice recording simulation state
    var isRecordingVoice by remember { mutableStateOf(false) }
    var recordingTimerSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            recordingTimerSeconds = 0
            while (isRecordingVoice) {
                delay(1000)
                recordingTimerSeconds++
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (chat == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Conversation not found")
        }
        return
    }

    val isGroup = chat.category == ChatCategory.GROUP || chat.category == ChatCategory.CHANNEL

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                if (isGroup) {
                                    onOpenGroupInfo(chat.id)
                                } else if (chat.directUser != null) {
                                    viewModel.openUserProfile(chat.directUser)
                                }
                            }
                    ) {
                        AvatarView(
                            name = chat.name,
                            avatarUrl = chat.avatarUrl ?: chat.directUser?.avatarUrl,
                            colorHex = chat.avatarColorHex,
                            size = 38.dp,
                            isOnline = chat.directUser?.isOnline == true,
                            showOnlineBadge = !isGroup,
                            isVerified = chat.directUser?.isVerified == true || chat.isVerifiedBadge,
                            isVip = chat.directUser?.isVip == true
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = chat.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            val statusSubtitle = if (chat.isTyping) {
                                SaloneDictionary.getTypingText(currentLang, chat.typingUserName ?: "Someone")
                            } else if (isGroup) {
                                "${chat.members.size} members 🇸🇱"
                            } else if (chat.directUser?.isOnline == true) {
                                SaloneDictionary.getOnlineStatus(currentLang)
                            } else {
                                chat.directUser?.lastSeenText ?: "Offline"
                            }
                            Text(
                                text = statusSubtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (chat.isTyping || chat.directUser?.isOnline == true) SaloneEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val peer = chat.directUser ?: chat.members.firstOrNull()?.user ?: viewModel.currentUser.value
                        viewModel.repository.startCall(peer, CallMediaType.VOICE, isGroup, chat.name)
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = SaloneEmeraldPrimary)
                    }
                    IconButton(onClick = {
                        val peer = chat.directUser ?: chat.members.firstOrNull()?.user ?: viewModel.currentUser.value
                        viewModel.repository.startCall(peer, CallMediaType.VIDEO, isGroup, chat.name)
                    }) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = SaloneEmeraldPrimary)
                    }
                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }

                    DropdownMenu(
                        expanded = showOverflowMenu,
                        onDismissRequest = { showOverflowMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isGroup) "Group Info" else "View Contact") },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                            onClick = {
                                showOverflowMenu = false
                                if (isGroup) onOpenGroupInfo(chat.id) else chat.directUser?.let { viewModel.openUserProfile(it) }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Shared Media & Docs Gallery") },
                            leadingIcon = { Icon(Icons.Default.PermMedia, contentDescription = null, tint = SaloneEmeraldPrimary) },
                            onClick = {
                                showOverflowMenu = false
                                showMediaGallerySheet = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("✨ AI Summarize Conversation") },
                            leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SaloneEmeraldPrimary) },
                            onClick = {
                                showOverflowMenu = false
                                aiSummaryText = viewModel.repository.summarizeConversation(chatId)
                                showAiSummaryDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Search in Chat") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            onClick = {
                                showOverflowMenu = false
                                showChatSearch = !showChatSearch
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (chat.isMuted) "Unmute Notifications" else "Mute Notifications") },
                            leadingIcon = { Icon(if (chat.isMuted) Icons.Default.VolumeUp else Icons.Default.VolumeOff, contentDescription = null) },
                            onClick = {
                                showOverflowMenu = false
                                viewModel.repository.toggleMuteChat(chatId, "Always")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear Messages", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showOverflowMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Pinned Message Header Banner
            if (chat.pinnedMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val idx = messages.indexOfFirst { it.id == chat.pinnedMessage.id }
                            if (idx >= 0) {
                                coroutineScope.launch { listState.animateScrollToItem(idx) }
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PushPin, contentDescription = "Pinned", tint = SaloneEmeraldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pinned Message", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SaloneEmeraldPrimary)
                            Text(chat.pinnedMessage.text, fontSize = 12.sp, maxLines = 1)
                        }
                        IconButton(
                            onClick = { viewModel.repository.togglePinMessage(chatId, chat.pinnedMessage.id) },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Unpin", modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // In-Chat Search Bar
            if (showChatSearch) {
                SaloneSearchField(
                    query = inChatSearchQuery,
                    onQueryChange = { inChatSearchQuery = it },
                    placeholder = "Find words in this conversation...",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Messages LazyColumn
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // Encryption Banner
                item {
                    EncryptionBanner()
                }

                val displayedMessages = if (inChatSearchQuery.isBlank()) {
                    messages
                } else {
                    messages.filter { it.text.contains(inChatSearchQuery, ignoreCase = true) }
                }

                items(displayedMessages, key = { it.id }) { msg ->
                    MessageBubble(
                        message = msg,
                        isGroupChat = isGroup,
                        highlightQuery = inChatSearchQuery,
                        onMessageClick = { clicked ->
                            if (clicked.type == MessageType.IMAGE || clicked.type == MessageType.VIDEO || clicked.type == MessageType.DOCUMENT) {
                                viewModel.openMediaPreview(clicked)
                            }
                        },
                        onMessageLongClick = { clicked ->
                            selectedActionMessage = clicked
                        },
                        onReactionClick = { emoji ->
                            viewModel.repository.addReaction(chatId, msg.id, emoji)
                        },
                        onReplyClick = { replyRef ->
                            val target = messages.find { it.id == replyRef.messageId }
                            if (target != null) {
                                val idx = messages.indexOf(target)
                                coroutineScope.launch { listState.animateScrollToItem(idx) }
                            }
                        },
                        onTranslateClick = {
                            viewModel.repository.translateMessageToKrio(chatId, it.id)
                        }
                    )
                }
            }

            // Replying to banner
            AnimatedVisibility(visible = replyingToMessage != null) {
                replyingToMessage?.let { rMsg ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.width(3.dp).height(32.dp).background(SaloneEmeraldPrimary))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Replying to ${rMsg.senderName}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SaloneEmeraldPrimary)
                                Text(rMsg.text, fontSize = 11.sp, maxLines = 1)
                            }
                            IconButton(onClick = { replyingToMessage = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel Reply", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Quick Mention / Hashtag Chips if typing '@' or '#'
            if (textInput.endsWith("@")) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val sampleMentions = listOf("aminata_b", "mohamed_t", "fatmata_k", "samuel_k", "salone_pikin", "all")
                    items(sampleMentions) { handle ->
                        SuggestionChip(
                            onClick = { textInput += "$handle " },
                            label = { Text("@$handle", fontSize = 11.sp) }
                        )
                    }
                }
            } else if (textInput.endsWith("#")) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val sampleTags = listOf("Salone", "Freetown", "CottonTree", "Leone", "BoTown", "KrioPride")
                    items(sampleTags) { tag ->
                        SuggestionChip(
                            onClick = { textInput += "$tag " },
                            label = { Text("#$tag", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Bottom Chat Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emoji / Sticker Button
                        IconButton(onClick = { showEmojiPicker = !showEmojiPicker }) {
                            Icon(
                                imageVector = if (showEmojiPicker) Icons.Default.Keyboard else Icons.Default.EmojiEmotions,
                                contentDescription = "Emoji",
                                tint = if (showEmojiPicker) SaloneEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Attach Button (+)
                        IconButton(onClick = { showAttachmentsSheet = true }) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = "Attachment", tint = SaloneEmeraldPrimary)
                        }

                        // Voice recording in-progress view or text field
                        if (isRecordingVoice) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .background(Color(0xFFE11D48).copy(alpha = 0.15f), RoundedCornerShape(22.dp))
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFFE11D48))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Recording Salone Audio: 0:${recordingTimerSeconds.toString().padStart(2, '0')}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFE11D48)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    isRecordingVoice = false
                                    viewModel.repository.sendMessage(
                                        chatId = chatId,
                                        text = "Voice message",
                                        type = MessageType.VOICE_NOTE,
                                        audioData = AudioPayload(durationSeconds = recordingTimerSeconds.coerceAtLeast(3))
                                    )
                                },
                                modifier = Modifier.size(42.dp).background(SaloneEmeraldPrimary, CircleShape)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send Voice", tint = Color.White)
                            }
                        } else {
                            // Text Input Field
                            TextField(
                                value = textInput,
                                onValueChange = { textInput = it },
                                placeholder = {
                                    Text(
                                        if (editingMessage != null) "Edit message..." else "Message (@ to mention, # for tag)...",
                                        fontSize = 13.sp
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 44.dp, max = 110.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                maxLines = 4
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            // Send or Mic Button
                            if (textInput.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        val sendingText = textInput.trim()
                                        if (editingMessage != null) {
                                            viewModel.repository.editMessage(chatId, editingMessage!!.id, sendingText)
                                            editingMessage = null
                                        } else {
                                            val replyRef = replyingToMessage?.let {
                                                ReplyReference(
                                                    messageId = it.id,
                                                    senderName = it.senderName,
                                                    snippetText = it.text,
                                                    mediaType = it.type
                                                )
                                            }
                                            viewModel.repository.sendMessage(
                                                chatId = chatId,
                                                text = sendingText,
                                                type = MessageType.TEXT,
                                                replyTo = replyRef
                                            )
                                            replyingToMessage = null
                                        }
                                        textInput = ""
                                    },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(SaloneEmeraldPrimary, CircleShape)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                                }
                            } else {
                                IconButton(
                                    onClick = { isRecordingVoice = true },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(SaloneEmeraldPrimary.copy(alpha = 0.15f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = "Record Audio", tint = SaloneEmeraldPrimary)
                                }
                            }
                        }
                    }

                    // Emoji / Sticker / GIF Bottom Picker
                    if (showEmojiPicker) {
                        EmojiStickerPicker(
                            onEmojiSelected = { textInput += it },
                            onStickerSelected = { sticker ->
                                viewModel.repository.sendMessage(
                                    chatId = chatId,
                                    text = "${sticker.iconEmoji} ${sticker.title} - ${sticker.subText}",
                                    type = MessageType.STICKER
                                )
                                showEmojiPicker = false
                            },
                            onGifSelected = { gifLabel ->
                                viewModel.repository.sendMessage(
                                    chatId = chatId,
                                    text = gifLabel,
                                    type = MessageType.GIF,
                                    mediaUrl = "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?w=500"
                                )
                                showEmojiPicker = false
                            }
                        )
                    }
                }
            }
        }
    }

    // Attachments Bottom Sheet
    if (showAttachmentsSheet) {
        AttachmentBottomSheet(
            onDismiss = { showAttachmentsSheet = false },
            onOptionSelected = { action ->
                when (action) {
                    "PHOTO" -> {
                        viewModel.repository.sendMessage(
                            chatId = chatId,
                            text = "Beautiful Sierra Leone scenery 🌴",
                            type = MessageType.IMAGE,
                            mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800"
                        )
                    }
                    "CAMERA" -> {
                        viewModel.repository.sendMessage(
                            chatId = chatId,
                            text = "Live snapshot from Aberdeen Beach 📸",
                            type = MessageType.IMAGE,
                            mediaUrl = "https://images.unsplash.com/photo-1519046904884-53103b34b206?w=800"
                        )
                    }
                    "DOCUMENT" -> {
                        viewModel.repository.sendMessage(
                            chatId = chatId,
                            text = "Sierra Leone Tech & Development Blueprint 2025",
                            type = MessageType.DOCUMENT,
                            documentData = DocumentPayload(
                                fileName = "Salone_Vision_2025.pdf",
                                fileSizeText = "4.2 MB",
                                fileExtension = "PDF"
                            )
                        )
                    }
                    "AUDIO" -> {
                        viewModel.repository.sendMessage(
                            chatId = chatId,
                            text = "Salone Afro-Beat Track 🎶",
                            type = MessageType.AUDIO,
                            audioData = AudioPayload(durationSeconds = 180, isVoiceNote = false)
                        )
                    }
                    "LOCATION" -> {
                        viewModel.repository.sendMessage(
                            chatId = chatId,
                            text = "Cotton Tree Historic Monument",
                            type = MessageType.LOCATION,
                            locationData = LocationPayload(
                                latitude = 8.4871,
                                longitude = -13.2356,
                                placeName = "Cotton Tree Plaza",
                                address = "Siaka Stevens St, Freetown, Sierra Leone"
                            )
                        )
                    }
                    "CONTACT" -> {
                        viewModel.repository.sendMessage(
                            chatId = chatId,
                            text = "Shared Contact",
                            type = MessageType.CONTACT_CARD,
                            contactData = ContactPayload(
                                name = "Aminata Bangura",
                                phone = "+232 78 440 192",
                                organization = "Salon Na We Yon Ambassador"
                            )
                        )
                    }
                    "POLL" -> {
                        viewModel.repository.sendMessage(
                            chatId = chatId,
                            text = "📊 Community Poll: Where should we host the Salone Tech Festival?\n1️⃣ Lumley Beach\n2️⃣ Bintumani Conference Center\n3️⃣ Bo Town Stadium",
                            type = MessageType.TEXT
                        )
                    }
                }
            }
        )
    }

    // Message Actions Sheet on Long Press
    selectedActionMessage?.let { msg ->
        MessageActionsBottomSheet(
            message = msg,
            onDismiss = { selectedActionMessage = null },
            onReaction = { emoji ->
                viewModel.repository.addReaction(chatId, msg.id, emoji)
            },
            onReply = {
                replyingToMessage = msg
            },
            onCopy = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Salone Message", msg.text)
                clipboard.setPrimaryClip(clip)
            },
            onStar = {
                viewModel.repository.toggleStarMessage(chatId, msg.id)
            },
            onPin = {
                viewModel.repository.togglePinMessage(chatId, msg.id)
            },
            onForward = {
                viewModel.startForwardMessage(chatId, msg)
            },
            onEdit = {
                editingMessage = msg
                textInput = msg.text
            },
            onTranslate = {
                viewModel.repository.translateMessageToKrio(chatId, msg.id)
            },
            onDelete = { forEveryone ->
                viewModel.repository.deleteMessage(chatId, msg.id, forEveryone)
            },
            onReport = {
                reportTargetMsg = msg
                showReportDialog = true
            }
        )
    }

    // AI Summary Dialog
    if (showAiSummaryDialog) {
        AlertDialog(
            onDismissRequest = { showAiSummaryDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SaloneEmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salone AI Summary")
                }
            },
            text = {
                Text(aiSummaryText, style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                Button(
                    onClick = { showAiSummaryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary)
                ) {
                    Text("Done")
                }
            }
        )
    }

    // Report User / Message Dialog
    if (showReportDialog && reportTargetMsg != null) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report Message or User 🛡️") },
            text = {
                Column {
                    Text("Select report reason:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("Spam or Fraud", "Harassment / Abusive", "Inappropriate Content", "Misinformation").forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reportReason = reason }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = reportReason == reason,
                                onClick = { reportReason = reason }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(reason, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.repository.reportUser(
                            reportedUserId = reportTargetMsg!!.senderId,
                            messageSnippet = reportTargetMsg!!.text,
                            reason = reportReason
                        )
                        showReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Submit Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // In-Chat Shared Media Gallery Sheet
    if (showMediaGallerySheet) {
        InChatMediaGallerySheet(
            messages = messages,
            onDismiss = { showMediaGallerySheet = false },
            onSelectMessage = { msg ->
                viewModel.openMediaPreview(msg)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InChatMediaGallerySheet(
    messages: List<Message>,
    onDismiss: () -> Unit,
    onSelectMessage: (Message) -> Unit
) {
    var selectedMediaTab by remember { mutableIntStateOf(0) } // 0: Media (Photos/Videos), 1: Docs, 2: Audio, 3: Links

    val mediaItems = remember(messages) {
        messages.filter { it.type == MessageType.IMAGE || it.type == MessageType.VIDEO }
    }
    val docItems = remember(messages) {
        messages.filter { it.type == MessageType.DOCUMENT }
    }
    val audioItems = remember(messages) {
        messages.filter { it.type == MessageType.AUDIO || it.type == MessageType.VOICE_NOTE }
    }
    val linkItems = remember(messages) {
        messages.filter { it.text.contains("http://") || it.text.contains("https://") }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Conversation Media Gallery 🇸🇱",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(10.dp))

            TabRow(
                selectedTabIndex = selectedMediaTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = SaloneEmeraldPrimary,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(selected = selectedMediaTab == 0, onClick = { selectedMediaTab = 0 }, text = { Text("Media (${mediaItems.size})", fontSize = 11.sp) })
                Tab(selected = selectedMediaTab == 1, onClick = { selectedMediaTab = 1 }, text = { Text("Docs (${docItems.size})", fontSize = 11.sp) })
                Tab(selected = selectedMediaTab == 2, onClick = { selectedMediaTab = 2 }, text = { Text("Audio (${audioItems.size})", fontSize = 11.sp) })
                Tab(selected = selectedMediaTab == 3, onClick = { selectedMediaTab = 3 }, text = { Text("Links (${linkItems.size})", fontSize = 11.sp) })
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.heightIn(min = 200.dp, max = 360.dp)) {
                when (selectedMediaTab) {
                    0 -> {
                        if (mediaItems.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No shared photos or videos yet", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(mediaItems) { msg ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().clickable { onSelectMessage(msg) },
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    ) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (msg.type == MessageType.VIDEO) Icons.Default.Videocam else Icons.Default.Image,
                                                contentDescription = null,
                                                tint = SaloneEmeraldPrimary,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(msg.text.ifBlank { if (msg.type == MessageType.VIDEO) "Video file" else "Photo" }, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text("Sent by ${msg.senderName} • ${msg.timeFormatted}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        if (docItems.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No documents or PDFs shared yet", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(docItems) { msg ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().clickable { onSelectMessage(msg) },
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    ) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF0066B2), modifier = Modifier.size(28.dp))
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(msg.documentData?.fileName ?: "Sierra_Leone_Doc.pdf", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text("${msg.documentData?.fileSizeText ?: "3.2 MB"} • ${msg.timeFormatted}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Icon(Icons.Default.Download, contentDescription = null, tint = SaloneEmeraldPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        if (audioItems.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No audio notes or music shared yet", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(audioItems) { msg ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().clickable { onSelectMessage(msg) },
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    ) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Mic, contentDescription = null, tint = SaloneEmeraldPrimary, modifier = Modifier.size(28.dp))
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Voice Note (${msg.audioData?.durationSeconds ?: 15}s)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text("${msg.senderName} • ${msg.timeFormatted}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = SaloneEmeraldPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        if (linkItems.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No links shared in this conversation yet", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(linkItems) { msg ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    ) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Link, contentDescription = null, tint = SaloneEmeraldPrimary)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(msg.text, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1)
                                                Text("Sent by ${msg.senderName}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
