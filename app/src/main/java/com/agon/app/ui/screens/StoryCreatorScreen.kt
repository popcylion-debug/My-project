package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.viewmodel.MainViewModel

@Composable
fun StoryCreatorScreen(
    viewModel: MainViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var statusText by remember { mutableStateOf("") }
    var selectedGradientIndex by remember { mutableIntStateOf(0) }

    val gradientPresets = listOf(
        listOf("#008751", "#0066B2") to "Sierra Leone Flag",
        listOf("#FBB034", "#E11D48") to "Salone Sunset",
        listOf("#008751", "#FBB034") to "Palm Palm",
        listOf("#8B5CF6", "#EC4899") to "Neon Freetown",
        listOf("#0F172A", "#334155") to "Midnight Atlantic"
    )

    val currentColors = gradientPresets[selectedGradientIndex].first.map {
        Color(android.graphics.Color.parseColor(it))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(currentColors))
            .padding(24.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            Text("Post Salone Status 🇸🇱", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            IconButton(
                onClick = {
                    selectedGradientIndex = (selectedGradientIndex + 1) % gradientPresets.size
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(Icons.Default.FormatColorFill, contentDescription = "Change Color", tint = Color.White)
            }
        }

        // Center Text Input
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 100.dp),
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = statusText,
                onValueChange = { statusText = it },
                placeholder = {
                    Text(
                        "Type a status update...\nWetin dey kam na Salone? 🇸🇱",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Bottom Controls: Color Palettes & Post FAB
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                items(gradientPresets.indices.toList()) { idx ->
                    val pair = gradientPresets[idx]
                    val c1 = Color(android.graphics.Color.parseColor(pair.first[0]))
                    val c2 = Color(android.graphics.Color.parseColor(pair.first[1]))
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(c1, c2)))
                            .then(
                                if (selectedGradientIndex == idx) Modifier.background(Color.White.copy(alpha = 0.3f), CircleShape)
                                else Modifier
                            )
                            .clickable { selectedGradientIndex = idx }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FloatingActionButton(
                    onClick = {
                        if (statusText.isNotBlank()) {
                            viewModel.repository.addStory(
                                text = statusText,
                                gradientHex = gradientPresets[selectedGradientIndex].first
                            )
                            onClose()
                        }
                    },
                    containerColor = Color.White,
                    contentColor = SaloneEmeraldPrimary
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Post Status")
                }
            }
        }
    }
}
