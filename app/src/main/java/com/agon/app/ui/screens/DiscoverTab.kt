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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.ChatCategory
import com.agon.app.data.models.User
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.components.SaloneSearchField
import com.agon.app.ui.theme.SaloneAtlanticBlue
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold
import com.agon.app.viewmodel.MainViewModel

data class PublicSaloneChannel(
    val id: String,
    val name: String,
    val description: String,
    val subscribersCount: String,
    val iconEmoji: String,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverTab(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddContactDialog by remember { mutableStateOf(false) }

    // Add contact form inputs
    var newContactName by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("+232 ") }
    var newContactTribe by remember { mutableStateOf("Freetown (Western Area)") }

    val publicChannels = listOf(
        PublicSaloneChannel("pub_1", "🇸🇱 Salone Breaking News HQ", "Verified updates from Sierra Leone, Freetown City Council & ministries.", "142.5K members", "📰", "News"),
        PublicSaloneChannel("pub_2", "🎶 Freetown Vibez & Sound", "Latest Afro-Beats, Bubu rhythms, live concerts and beach festivals.", "89.2K members", "🎵", "Music"),
        PublicSaloneChannel("pub_3", "⚽ Leone Stars & Premier League", "Live commentary, Sierra Leone national football team discussions & fixtures.", "112.4K members", "⚽", "Sports"),
        PublicSaloneChannel("pub_4", "💻 Salone Tech & Builders", "Software engineering, startups, fintech and digital innovation across Sierra Leone.", "45.8K members", "💻", "Tech"),
        PublicSaloneChannel("pub_5", "🛍️ Sierra Leone Deals & Trade", "Authentic Ronko fabrics, tech gadgets, vehicles and real estate.", "78.1K members", "🛍️", "Commerce"),
        PublicSaloneChannel("pub_6", "🌴 Krio & Salone Culture Hub", "Preserving traditional proverbs, history of Cotton Tree, Bunce Island & heritage.", "34.6K members", "🌳", "Culture")
    )

    val trendingTags = listOf("#SaloneNaWeYon", "#FreetownTech", "#CottonTree", "#LeoneStars", "#SweetSalone", "#LumleyBeach", "#BoTownHub")

    val otherUsers = remember(allUsers, currentUser) {
        allUsers.filter { it.id != currentUser.id }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Search field
            item {
                SaloneSearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Discover Salone channels, public groups & users...",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Quick Actions Bar: Add Contact & QR Code
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SaloneEmeraldPrimary.copy(alpha = 0.12f),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showAddContactDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = SaloneEmeraldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Contact", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SaloneEmeraldPrimary)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SaloneAtlanticBlue.copy(alpha = 0.12f),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setQrShareModalVisible(true) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = SaloneAtlanticBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("My Salone QR", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SaloneAtlanticBlue)
                        }
                    }
                }
            }

            // Trending Sierra Leone Topics
            item {
                Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                    Text(
                        text = "Trending in Sierra Leone 🇸🇱",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(trendingTags) { tag ->
                            SuggestionChip(
                                onClick = { searchQuery = tag },
                                label = { Text(tag, fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            // Public Groups & Channels
            item {
                Text(
                    text = "Public Channels & Communities",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            items(publicChannels.filter { it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true) }) { channel ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(SaloneEmeraldPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = channel.iconEmoji, fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(channel.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(channel.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(channel.subscribersCount, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = SaloneEmeraldPrimary)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                val newChatId = viewModel.repository.createGroup(
                                    name = channel.name,
                                    description = channel.description,
                                    selectedUserIds = otherUsers.map { it.id },
                                    isAnnouncementsOnly = false
                                )
                                viewModel.openChat(newChatId)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Join", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Suggested People to Connect
            item {
                Text(
                    text = "Suggested Salone Contacts",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
                )
            }

            items(otherUsers) { user ->
                ListItem(
                    headlineContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(user.displayName, fontWeight = FontWeight.SemiBold)
                            if (user.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = SaloneEmeraldPrimary, modifier = Modifier.size(14.dp))
                            }
                        }
                    },
                    supportingContent = { Text("${user.tribeOrLocation} • @${user.username}", fontSize = 11.sp) },
                    leadingContent = {
                        AvatarView(name = user.displayName, size = 42.dp)
                    },
                    trailingContent = {
                        Button(
                            onClick = {
                                val chatId = viewModel.repository.getOrCreateDirectChat(user)
                                viewModel.openChat(chatId)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Chat", fontSize = 11.sp)
                        }
                    }
                )
            }
        }
    }

    // Add Contact Sheet / Dialog
    if (showAddContactDialog) {
        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = SaloneEmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Salone Contact")
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newContactName,
                        onValueChange = { newContactName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newContactPhone,
                        onValueChange = { newContactPhone = it },
                        label = { Text("Phone Number (+232...)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newContactTribe,
                        onValueChange = { newContactTribe = it },
                        label = { Text("Location / Tribe") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newContactName.isNotBlank()) {
                            viewModel.repository.addContact(newContactName, newContactPhone, newContactTribe)
                            showAddContactDialog = false
                            newContactName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary)
                ) {
                    Text("Save Contact")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
