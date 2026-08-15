package com.agon.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.agon.app.ui.components.Avatar
import com.agon.app.ui.components.EmptyState
import com.agon.app.ui.components.prettyTime
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(vm: AppViewModel, onOpenChat: (String) -> Unit, onNew: () -> Unit) {
    val s by vm.strings.collectAsState()
    val messages by vm.messages.collectAsState()
    val people by vm.people.collectAsState()
    val convos = vm.conversations()

    Scaffold(
        topBar = { TopAppBar(title = { Text(s.chats) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNew) {
                Icon(Icons.Default.Edit, contentDescription = s.newChat)
            }
        },
    ) { pad ->
        if (convos.isEmpty()) {
            EmptyState(s.chats, s.emptyChats, Modifier.fillMaxSize().padding(pad))
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(bottom = 88.dp)) {
                items(convos, key = { it.conversationId }) { c ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onOpenChat(c.peerId) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Avatar(c.peerName, c.peerPhoto, online = c.online)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(c.peerName, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                c.lastText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Text(prettyTime(c.lastAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}
