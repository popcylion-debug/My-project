package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.MessageType
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.components.SaloneSearchField
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold
import com.agon.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("All") } // All, Messages, Media, Docs, Audio, Links

    val chats by viewModel.chats.collectAsState()
    val allMessages by viewModel.messages.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    val tabs = listOf("All", "Messages", "Media", "Docs", "Audio", "Links")

    // Flatten all messages
    val flattenedMessages = remember(allMessages) {
        allMessages.flatMap { it.value }
    }

    val matchedMessages = remember(flattenedMessages, searchQuery, selectedTab) {
        if (searchQuery.isBlank()) emptyList()
        else {
            flattenedMessages.filter { msg ->
                val matchQuery = msg.text.contains(searchQuery, ignoreCase = true) ||
                        (msg.documentData?.fileName?.contains(searchQuery, ignoreCase = true) == true) ||
                        (msg.locationData?.placeName?.contains(searchQuery, ignoreCase = true) == true)

                val matchTab = when (selectedTab) {
                    "Messages" -> msg.type == MessageType.TEXT
                    "Media" -> msg.type == MessageType.IMAGE || msg.type == MessageType.VIDEO
                    "Docs" -> msg.type == MessageType.DOCUMENT
                    "Audio" -> msg.type == MessageType.AUDIO || msg.type == MessageType.VOICE_NOTE
                    else -> true
                }

                matchQuery && matchTab
            }
        }
    }

    val matchedUsers = remember(allUsers, searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else allUsers.filter {
            it.displayName.contains(searchQuery, ignoreCase = true) ||
                    it.username.contains(searchQuery, ignoreCase = true) ||
                    it.tribeOrLocation.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Global Salone Search", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            SaloneSearchField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search messages, people, files, locations...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs) { tab ->
                    FilterChip(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab) }
                    )
                }
            }

            if (searchQuery.isBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Search Salon Na We Yon", fontWeight = FontWeight.SemiBold)
                        Text("Type to find messages, people and documents across Sierra Leone.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (matchedUsers.isNotEmpty()) {
                        item {
                            Text("Users & Contacts (${matchedUsers.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SaloneEmeraldPrimary)
                        }
                        items(matchedUsers) { user ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    val chatId = viewModel.repository.getOrCreateDirectChat(user)
                                    viewModel.openChat(chatId)
                                    onBack()
                                },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    AvatarView(name = user.displayName, size = 40.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(user.displayName, fontWeight = FontWeight.Bold)
                                        Text("@${user.username} • ${user.tribeOrLocation}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Messages & Files (${matchedMessages.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SaloneEmeraldPrimary)
                    }

                    if (matchedMessages.isEmpty()) {
                        item {
                            Text("No matching messages found for \"$searchQuery\"", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        items(matchedMessages) { msg ->
                            val chat = chats.find { it.id == msg.chatId }
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    viewModel.openChat(msg.chatId)
                                    onBack()
                                },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(chat?.name ?: msg.senderName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SaloneEmeraldPrimary)
                                        Text(msg.timeFormatted, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Highlight matching text
                                    val annotatedText = buildAnnotatedString {
                                        val fullText = msg.text
                                        val lowerFull = fullText.lowercase()
                                        val lowerQuery = searchQuery.lowercase()
                                        var startIndex = 0

                                        while (true) {
                                            val matchIndex = lowerFull.indexOf(lowerQuery, startIndex)
                                            if (matchIndex < 0) {
                                                append(fullText.substring(startIndex))
                                                break
                                            }
                                            append(fullText.substring(startIndex, matchIndex))
                                            withStyle(SpanStyle(background = SaloneGold.copy(alpha = 0.35f), fontWeight = FontWeight.Bold)) {
                                                append(fullText.substring(matchIndex, matchIndex + searchQuery.length))
                                            }
                                            startIndex = matchIndex + searchQuery.length
                                        }
                                    }

                                    Text(text = annotatedText, fontSize = 12.sp, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
