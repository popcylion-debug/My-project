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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.StoryItem
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold
import com.agon.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesTab(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val stories by viewModel.stories.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val myStories = remember(stories) { stories.filter { it.isMine } }
    val otherStories = remember(stories) { stories.filter { !it.isMine } }

    var showPrivacyModal by remember { mutableStateOf(false) }
    var privacyChoice by remember { mutableStateOf("My Contacts") }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // My Status Section
            item {
                Text(
                    text = "My Status",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (myStories.isNotEmpty()) {
                                viewModel.openStory(myStories.first())
                            } else {
                                viewModel.setStoryCreatorVisible(true)
                            }
                        },
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "My Status 🇸🇱",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            if (myStories.isNotEmpty()) {
                                Text(
                                    text = "${myStories.first().viewers.size} views • ${myStories.first().timeAgoFormatted}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "Tap to add status update (Expires in 24h)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = { showPrivacyModal = true }) {
                            Icon(Icons.Default.Lock, contentDescription = "Privacy", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // Recent Updates Section
            item {
                Text(
                    text = "Recent Updates",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                )
            }

            if (otherStories.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.HistoryToggleOff, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No recent status updates", fontWeight = FontWeight.SemiBold)
                        Text("When your contacts post, they will appear here.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(otherStories, key = { it.id }) { story ->
                    StoryItemRow(
                        story = story,
                        onClick = { viewModel.openStory(story) }
                    )
                }
            }
        }

        // Floating Action Buttons (Text & Media Creator)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 86.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SmallFloatingActionButton(
                onClick = { viewModel.setStoryCreatorVisible(true) },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Text Status")
            }

            FloatingActionButton(
                onClick = {
                    viewModel.repository.addStory(
                        text = "Live from Freetown! 🌊🌴",
                        gradientHex = listOf("#008751", "#0066B2"),
                        mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
                        caption = "Aberdeen vibes on Salon Na We Yon"
                    )
                },
                containerColor = SaloneEmeraldPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Camera Status")
            }
        }
    }

    // Status Privacy Modal
    if (showPrivacyModal) {
        AlertDialog(
            onDismissRequest = { showPrivacyModal = false },
            title = { Text("Status Privacy Controls 🇸🇱") },
            text = {
                Column {
                    Text("Who can see my status updates:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    listOf("My Contacts", "My Contacts Except...", "Only Share With...").forEach { opt ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { privacyChoice = opt }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = privacyChoice == opt,
                                onClick = { privacyChoice = opt }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(opt)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary)
                ) {
                    Text("Save")
                }
            }
        )
    }
}

@Composable
fun StoryItemRow(
    story: StoryItem,
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
            Box(
                modifier = Modifier
                    .size(54.dp)
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

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = story.userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = story.timeAgoFormatted,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
