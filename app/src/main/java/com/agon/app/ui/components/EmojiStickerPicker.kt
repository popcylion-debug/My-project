package com.agon.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.ui.theme.SaloneEmeraldPrimary

data class SaloneSticker(
    val title: String,
    val iconEmoji: String,
    val subText: String
)

@Composable
fun EmojiStickerPicker(
    onEmojiSelected: (String) -> Unit,
    onStickerSelected: (SaloneSticker) -> Unit,
    onGifSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Emojis, 1: Salone Stickers, 2: GIFs

    val standardEmojis = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰",
        "😘", "😗", "😙", "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🥳", "😏", "😒",
        "😞", "😔", "😟", "😕", "🙁", "😣", "😖", "😫", "😩", "🥺", "😢", "😭", "😤", "😠", "😡", "🤬",
        "👍", "👎", "👌", "🤌", "✌️", "🤞", "🤟", "🤘", "🤙", "👈", "👉", "👆", "👇", "☝️", "✋", "🤚",
        "🇸🇱", "🦁", "🌴", "🏖️", "⚽", "🍛", "🥑", "🍌", "🥭", "🥥", "💎", "👑", "🔥", "✨", "💯", "❤️"
    )

    val saloneStickers = listOf(
        SaloneSticker("Cotton Tree", "🌳", "Freedom Landmark"),
        SaloneSticker("Salone Pride", "🇸🇱", "Green White Blue"),
        SaloneSticker("Salone Lion", "🦁", "Roar of the Brave"),
        SaloneSticker("Lumley Beach", "🏖️", "Freetown Coast"),
        SaloneSticker("Poda Poda", "🚐", "Classic Salone Ride"),
        SaloneSticker("Sweet Plasas", "🍲", "Cassava & Groundnut"),
        SaloneSticker("Ronko Spirit", "👘", "Traditional Attire"),
        SaloneSticker("Salone Music", "🎵", "Bubu & Afro-Beats"),
        SaloneSticker("Kusheh Bro", "🤝", "Friendly Salone Greeting"),
        SaloneSticker("Krio Vibes", "✨", "Salon Na We Yon")
    )

    val popularGifs = listOf(
        "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?w=300" to "Salone Dance Party 💃",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=300" to "Aberdeen Waves 🌊",
        "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=300" to "Celebration Sparklers ✨",
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300" to "African Beats Studio 🎧"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = SaloneEmeraldPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.EmojiEmotions, contentDescription = "Emoji") },
                    text = { Text("Emojis", fontSize = 11.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Stickers") },
                    text = { Text("Salone Stickers 🇸🇱", fontSize = 11.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Gif, contentDescription = "GIFs") },
                    text = { Text("GIFs", fontSize = 11.sp) }
                )
            }

            Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                when (selectedTab) {
                    0 -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 40.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(standardEmojis) { emoji ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable { onEmojiSelected(emoji) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 22.sp)
                                }
                            }
                        }
                    }

                    1 -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(saloneStickers) { sticker ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onStickerSelected(sticker) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = sticker.iconEmoji, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(sticker.title, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            Text(sticker.subText, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(popularGifs) { gifItem ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(85.dp)
                                        .clickable { onGifSelected(gifItem.second) }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize().background(SaloneEmeraldPrimary.copy(alpha = 0.15f))
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.Gif, contentDescription = null, tint = SaloneEmeraldPrimary, modifier = Modifier.size(32.dp))
                                            Text(gifItem.second, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
