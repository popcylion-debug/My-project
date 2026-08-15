package com.agon.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.agon.app.R
import com.agon.app.ui.components.hapticTick
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskScreen(vm: AppViewModel) {
    val s by vm.strings.collectAsState()
    val settings by vm.settings.collectAsState()
    val turns by vm.ai.collectAsState()
    val context = LocalContext.current
    var draft by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(turns.size) {
        if (turns.isNotEmpty()) listState.animateScrollToItem(turns.lastIndex)
    }

    Scaffold(topBar = { TopAppBar(title = { Text(s.ask) }) }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).imePadding()) {
            if (turns.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    Image(
                        painterResource(R.drawable.img_lion),
                        null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Box(
                        Modifier.fillMaxSize().background(
                            Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.background)),
                        ),
                    )
                    Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                        Text("Ask SL", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
                        Text(s.askEmpty, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item { Spacer(Modifier.height(8.dp)) }
                    items(turns, key = { it.id }) { turn ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = if (turn.fromUser) Arrangement.End else Arrangement.Start) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (turn.fromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.widthIn(max = 320.dp),
                            ) {
                                Text(
                                    turn.text,
                                    modifier = Modifier.padding(12.dp),
                                    color = if (turn.fromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(s.askHint) },
                    maxLines = 4,
                    shape = RoundedCornerShape(24.dp),
                )
                IconButton(
                    onClick = {
                        val q = draft.trim()
                        if (q.isNotBlank()) {
                            hapticTick(context, settings.haptic)
                            vm.ask(q)
                            draft = ""
                        }
                    },
                ) { Icon(Icons.AutoMirrored.Filled.Send, s.send) }
            }
        }
    }
}
