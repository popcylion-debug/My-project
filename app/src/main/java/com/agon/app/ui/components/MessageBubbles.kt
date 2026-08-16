package com.agon.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Forward
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.agon.app.data.models.*
import com.agon.app.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: Message,
    isGroupChat: Boolean = false,
    highlightQuery: String = "",
    onMessageClick: (Message) -> Unit,
    onMessageLongClick: (Message) -> Unit,
    onReactionClick: (String) -> Unit,
    onReplyClick: (ReplyReference) -> Unit,
    onTranslateClick: (Message) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPlayingAudio by remember { mutableStateOf(false) }
    var audioProgress by remember { mutableFloatStateOf(0.35f) }
    var audioSpeed by remember { mutableStateOf("1x") }

    val isOutgoing = message.isOutgoing

    val bubbleShape = if (isOutgoing) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    val bubbleBackground = if (isOutgoing) {
        if (MaterialTheme.colorScheme.surface == DarkSurface) ChatBubbleSentDark else ChatBubbleSentLight
    } else {
        if (MaterialTheme.colorScheme.surface == DarkSurface) ChatBubbleReceivedDark else ChatBubbleReceivedLight
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp, horizontal = 12.dp),
        horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start
    ) {
        // Pinned badge indicator
        if (message.isPinned) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Pinned",
                    modifier = Modifier.size(12.dp),
                    tint = SaloneEmeraldPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Pinned message", fontSize = 10.sp, color = SaloneEmeraldPrimary, fontWeight = FontWeight.Bold)
            }
        }

        // Forwarded from indicator
        if (!message.forwardFrom.isNullOrBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Forward,
                    contentDescription = "Forwarded",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Forwarded", fontSize = 10.sp, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Main Bubble Surface
        Surface(
            shape = bubbleShape,
            color = bubbleBackground,
            shadowElevation = 1.dp,
            modifier = Modifier
                .widthIn(max = 310.dp)
                .combinedClickable(
                    onClick = { onMessageClick(message) },
                    onLongClick = { onMessageLongClick(message) }
                )
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {

                // Sender Name in Group
                if (isGroupChat && !isOutgoing) {
                    Text(
                        text = message.senderName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaloneEmeraldPrimary,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }

                // Quoted Reply preview
                if (message.replyTo != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                            .clickable { onReplyClick(message.replyTo) }
                    ) {
                        Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(28.dp)
                                    .background(SaloneEmeraldPrimary, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = message.replyTo.senderName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SaloneEmeraldPrimary
                                )
                                Text(
                                    text = message.replyTo.snippetText,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Payload according to MessageType
                when (message.type) {
                    MessageType.TEXT -> {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    MessageType.VOICE_NOTE, MessageType.AUDIO -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            IconButton(
                                onClick = { isPlayingAudio = !isPlayingAudio },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SaloneEmeraldPrimary, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(20.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    val heights = listOf(0.3f, 0.7f, 0.9f, 0.4f, 0.8f, 0.6f, 0.3f, 0.85f, 0.5f, 0.7f, 0.4f, 0.9f, 0.3f, 0.6f)
                                    heights.forEachIndexed { idx, h ->
                                        val isPassed = idx.toFloat() / heights.size <= audioProgress
                                        Box(
                                            modifier = Modifier
                                                .width(3.dp)
                                                .fillMaxHeight(h)
                                                .background(
                                                    if (isPassed) SaloneEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                                    RoundedCornerShape(1.dp)
                                                )
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isPlayingAudio) "0:12" else "0:24",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Voice Note 🇸🇱",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SaloneEmeraldPrimary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable {
                                    audioSpeed = when (audioSpeed) {
                                        "1x" -> "1.5x"
                                        "1.5x" -> "2x"
                                        else -> "1x"
                                    }
                                }
                            ) {
                                Text(
                                    text = audioSpeed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    MessageType.IMAGE, MessageType.VIDEO -> {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                if (message.mediaUrl != null) {
                                    AsyncImage(
                                        model = message.mediaUrl,
                                        contentDescription = "Media",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = if (message.type == MessageType.VIDEO) Icons.Default.Videocam else Icons.Default.Image,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = SaloneEmeraldPrimary
                                    )
                                }
                                if (message.type == MessageType.VIDEO) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                                    }
                                }
                            }
                            if (!message.text.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = message.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    MessageType.DOCUMENT -> {
                        val doc = message.documentData
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                                .padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(SaloneAtlanticBlue, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Description, contentDescription = "Doc", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = doc?.fileName ?: "Sierra_Leone_Doc.pdf",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${doc?.fileSizeText ?: "2.4 MB"} • ${doc?.fileExtension ?: "PDF"}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { onMessageClick(message) }) {
                                Icon(Icons.Default.Download, contentDescription = "Download", tint = SaloneEmeraldPrimary)
                            }
                        }
                    }

                    MessageType.LOCATION -> {
                        val loc = message.locationData
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFFE11D48), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = "Pin", tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = loc?.placeName ?: "Freetown, Sierra Leone",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = loc?.address ?: "Western Area Urban",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { onMessageClick(message) },
                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Open in Salone Map 🗺️", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }

                    MessageType.CONTACT_CARD -> {
                        val contact = message.contactData
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            AvatarView(name = contact?.name ?: "Contact", size = 36.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(contact?.name ?: "Salone Contact", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(contact?.phone ?: "+232 76 000 000", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = { onMessageClick(message) },
                                modifier = Modifier.height(28.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Add", fontSize = 10.sp)
                            }
                        }
                    }

                    MessageType.SYSTEM_NOTICE -> {
                        Text(
                            text = message.text,
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    else -> {
                        Text(message.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // AI Translated Text
                if (!message.translatedText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SaloneEmeraldContainer.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = message.translatedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SaloneEmeraldOnContainer,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

                // Timestamp & Checkmarks Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.isEdited) {
                        Text(
                            text = "edited ",
                            fontSize = 9.sp,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (message.isStarred) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Starred",
                            modifier = Modifier.size(11.dp),
                            tint = SaloneGold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    Text(
                        text = message.timeFormatted,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isOutgoing) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = if (message.status == MessageStatus.READ) ChatDoubleCheckBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Reactions Row
        if (message.reactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .offset(y = (-8).dp)
                    .padding(start = if (isOutgoing) 0.dp else 8.dp, end = if (isOutgoing) 8.dp else 0.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                message.reactions.forEach { reaction ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (reaction.hasReacted) SaloneEmeraldContainer else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        shadowElevation = 2.dp,
                        modifier = Modifier.clickable { onReactionClick(reaction.emoji) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = reaction.emoji, fontSize = 12.sp)
                            if (reaction.count > 1) {
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = reaction.count.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (reaction.hasReacted) SaloneEmeraldOnContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageActionsBottomSheet(
    message: Message,
    onDismiss: () -> Unit,
    onReaction: (String) -> Unit,
    onReply: () -> Unit,
    onCopy: () -> Unit,
    onStar: () -> Unit,
    onPin: () -> Unit,
    onForward: () -> Unit,
    onEdit: () -> Unit,
    onTranslate: () -> Unit,
    onDelete: (Boolean) -> Unit,
    onReport: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Quick Emojis
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("👍", "❤️", "😂", "😮", "😢", "🇸🇱", "🔥", "🙏").forEach { emoji ->
                    Surface(
                        modifier = Modifier
                            .size(42.dp)
                            .clickable {
                                onReaction(emoji)
                                onDismiss()
                            },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }
            }

            HorizontalDivider()

            // Action Items
            ListItem(
                headlineContent = { Text("Reply") },
                leadingContent = { Icon(Icons.Outlined.Reply, contentDescription = null) },
                modifier = Modifier.clickable {
                    onReply()
                    onDismiss()
                }
            )
            ListItem(
                headlineContent = { Text("Copy Text") },
                leadingContent = { Icon(Icons.Outlined.ContentCopy, contentDescription = null) },
                modifier = Modifier.clickable {
                    onCopy()
                    onDismiss()
                }
            )
            ListItem(
                headlineContent = { Text(if (message.isStarred) "Unstar" else "Star Message") },
                leadingContent = { Icon(Icons.Outlined.Star, contentDescription = null, tint = SaloneGold) },
                modifier = Modifier.clickable {
                    onStar()
                    onDismiss()
                }
            )
            ListItem(
                headlineContent = { Text(if (message.isPinned) "Unpin" else "Pin Message") },
                leadingContent = { Icon(Icons.Outlined.PushPin, contentDescription = null) },
                modifier = Modifier.clickable {
                    onPin()
                    onDismiss()
                }
            )
            ListItem(
                headlineContent = { Text("Forward Message") },
                leadingContent = { Icon(Icons.Outlined.Forward, contentDescription = null) },
                modifier = Modifier.clickable {
                    onForward()
                    onDismiss()
                }
            )
            ListItem(
                headlineContent = { Text("Translate to Krio (Salone) 🇸🇱") },
                leadingContent = { Icon(Icons.Default.Translate, contentDescription = null, tint = SaloneEmeraldPrimary) },
                modifier = Modifier.clickable {
                    onTranslate()
                    onDismiss()
                }
            )

            if (message.isOutgoing) {
                ListItem(
                    headlineContent = { Text("Edit Message") },
                    leadingContent = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                    modifier = Modifier.clickable {
                        onEdit()
                        onDismiss()
                    }
                )
                ListItem(
                    headlineContent = { Text("Delete for Everyone", color = MaterialTheme.colorScheme.error) },
                    leadingContent = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.clickable {
                        onDelete(true)
                        onDismiss()
                    }
                )
            } else {
                ListItem(
                    headlineContent = { Text("Report Spam / Harassment", color = MaterialTheme.colorScheme.error) },
                    leadingContent = { Icon(Icons.Default.Report, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.clickable {
                        onReport()
                        onDismiss()
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
