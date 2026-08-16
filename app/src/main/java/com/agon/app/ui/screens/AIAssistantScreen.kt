package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.MessageType
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.components.MessageBubble
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val allMessages by viewModel.messages.collectAsState()
    val aiMessages = allMessages["chat_kriogpt"] ?: emptyList()
    val listState = rememberLazyListState()

    var inputPrompt by remember { mutableStateOf("") }

    val quickPrompts = listOf(
        "Aw yu do? (Translate common phrases to Krio)",
        "Tell me the history of Cotton Tree 🌳",
        "Best places to visit in Freetown & Bo 🏖️",
        "How to make Sierra Leone Cassava Leaf 🍲",
        "Draft a business payment reminder in Krio"
    )

    LaunchedEffect(aiMessages.size) {
        if (aiMessages.isNotEmpty()) {
            listState.animateScrollToItem(aiMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AvatarView(name = "KrioGPT AI", colorHex = "#008751", size = 36.dp, isVerified = true)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("KrioGPT AI Assistant 🇸🇱", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Sierra Leone AI Intelligence", fontSize = 11.sp, color = SaloneEmeraldPrimary)
                        }
                    }
                },
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
            // Quick Prompt Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickPrompts) { prompt ->
                    SuggestionChip(
                        onClick = {
                            viewModel.repository.sendMessage(
                                chatId = "chat_kriogpt",
                                text = prompt
                            )
                        },
                        label = { Text(prompt, fontSize = 11.sp) }
                    )
                }
            }

            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(aiMessages, key = { it.id }) { msg ->
                    MessageBubble(
                        message = msg,
                        onMessageClick = {},
                        onMessageLongClick = {},
                        onReactionClick = {},
                        onReplyClick = {},
                        onTranslateClick = {}
                    )
                }
            }

            // Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputPrompt,
                        onValueChange = { inputPrompt = it },
                        placeholder = { Text("Ask KrioGPT about Salone, translations...", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(22.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputPrompt.isNotBlank()) {
                                viewModel.repository.sendMessage(
                                    chatId = "chat_kriogpt",
                                    text = inputPrompt.trim()
                                )
                                inputPrompt = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(SaloneEmeraldPrimary, CircleShape)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}
