package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.ChatCategory
import com.agon.app.data.models.ChatRoom
import com.agon.app.data.models.MessageType
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.components.SaloneSearchField
import com.agon.app.ui.components.TypingIndicatorView
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold
import com.agon.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListTab(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val chats by viewModel.chats.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // All, Unread, Groups, Channels, AI Bot

    val filterCategories = listOf("All", "Unread", "Groups", "Channels", "AI Bot")

    val filteredChats = remember(chats, searchQuery, selectedFilter) {
        chats.filter { room ->
            val matchesSearch = room.name.contains(searchQuery, ignoreCase = true) ||
                    (room.lastMessage?.text?.contains(searchQuery, ignoreCase = true) == true)

            val matchesFilter = when (selectedFilter) {
                "Unread" -> room.unreadCount > 0
                "Groups" -> room.category == ChatCategory.GROUP
                "Channels" -> room.category == ChatCategory.CHANNEL
                "AI Bot" -> room.category == ChatCategory.AI_BOT
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Search Bar
            item {
                SaloneSearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search chats, groups & messages...",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Status / Story Tray at Top
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(
                        text = "Status Updates 🇸🇱",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // My Status Add item
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    viewModel.setStoryCreatorVisible(true)
                                }
                            ) {
                                Box(
                                    modifier = Modifier.size(56.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AvatarView(
                                        name = currentUser.displayName,
                                        avatarUrl = currentUser.avatarUrl,
                                        colorHex = currentUser.avatarColorHex,
                                        size = 52.dp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.BottomEnd)
                                            .clip(CircleShape)
                                            .background(SaloneEmeraldPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add Story", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("My Status", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        // Contact Stories
                        items(stories) { story ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    viewModel.openStory(story)
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(if (story.isSeenByMe) Color.LightGray else SaloneEmeraldPrimary)
                                        .padding(2.5.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AvatarView(
                                        name = story.userName,
                                        avatarUrl = story.userAvatar,
                                        colorHex = story.userAvatarColor,
                                        size = 46.dp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = story.userName.split(" ").firstOrNull() ?: "Status",
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            // Filter Chips
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterCategories) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaloneEmeraldPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = SaloneEmeraldPrimary
                            )
                        )
                    }
                }
            }

            // Chat Items
            if (filteredChats.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Forum, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No conversations found", fontWeight = FontWeight.SemiBold)
                        Text("Start a new chat with fellow Sierra Leoneans!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(filteredChats, key = { it.id }) { room ->
                    ChatRoomRow(
                        room = room,
                        onClick = { viewModel.openChat(room.id) }
                    )
                }
            }
        }

        // Floating Action Button for New Chat / Create Group
        FloatingActionButton(
            onClick = { viewModel.setCreateGroupSheetVisible(true) },
            containerColor = SaloneEmeraldPrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 86.dp, end = 16.dp)
        ) {
            Icon(Icons.Default.Chat, contentDescription = "New Chat")
        }
    }
}

@Composable
fun ChatRoomRow(
    room: ChatRoom,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            AvatarView(
                name = room.name,
                avatarUrl = room.avatarUrl ?: room.directUser?.avatarUrl,
                colorHex = room.avatarColorHex,
                size = 52.dp,
                isOnline = room.directUser?.isOnline == true,
                showOnlineBadge = room.category == ChatCategory.DIRECT,
                isVerified = room.directUser?.isVerified == true || room.isVerifiedBadge,
                isVip = room.directUser?.isVip == true
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Text(
                            text = room.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (room.isMuted) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.VolumeOff, contentDescription = "Muted", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Text(
                        text = room.lastMessage?.timeFormatted ?: "Now",
                        fontSize = 11.sp,
                        color = if (room.unreadCount > 0) SaloneEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (room.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (room.isTyping) {
                        TypingIndicatorView(text = room.typingUserName ?: "Typing...")
                    } else {
                        val previewText = when (room.lastMessage?.type) {
                            MessageType.VOICE_NOTE, MessageType.AUDIO -> "🎤 Voice note"
                            MessageType.IMAGE -> "📷 Photo"
                            MessageType.VIDEO -> "🎥 Video"
                            MessageType.DOCUMENT -> "📄 ${room.lastMessage.documentData?.fileName ?: "Document"}"
                            MessageType.LOCATION -> "📍 Location shared"
                            MessageType.CONTACT_CARD -> "👤 Contact card"
                            else -> room.lastMessage?.text ?: "Tap to open chat"
                        }
                        Text(
                            text = previewText,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (room.isPinned) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = SaloneEmeraldPrimary,
                                modifier = Modifier.size(15.dp).padding(end = 4.dp)
                            )
                        }
                        if (room.unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SaloneEmeraldPrimary)
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = room.unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
