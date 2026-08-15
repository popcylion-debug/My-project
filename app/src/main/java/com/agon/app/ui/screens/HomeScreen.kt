package com.agon.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.agon.app.R
import com.agon.app.data.MqttBus
import com.agon.app.ui.components.Avatar
import com.agon.app.ui.components.FlagStripe
import com.agon.app.ui.components.LinkPill
import com.agon.app.ui.components.prettyTime
import com.agon.app.viewmodel.AppViewModel

@Composable
fun HomeScreen(vm: AppViewModel, onOpen: (String) -> Unit) {
    val s by vm.strings.collectAsState()
    val me by vm.me.collectAsState()
    val people by vm.people.collectAsState()
    val statuses by vm.statuses.collectAsState()
    val link by vm.link.collectAsState()
    val live = vm.livePeople()
    val stories = vm.liveStatuses()
    val convos = vm.conversations()
    val onlineHint = people.size + statuses.size

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            Box(Modifier.fillMaxWidth().height(220.dp)) {
                Image(
                    painterResource(R.drawable.img_hills),
                    null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(0.15f), MaterialTheme.colorScheme.background),
                        ),
                    ),
                )
                Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                    Text("SALON NA WE YON", color = Color.White, fontWeight = FontWeight.Black)
                    Text(
                        "Kushe, ${me?.displayName ?: "stranger"}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(6.dp))
                    LinkPill(
                        if (onlineHint >= 0) vm.linkLabel() else vm.linkLabel(),
                        link == MqttBus.Link.Live,
                    )
                }
            }
            FlagStripe()
        }
        item {
            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                QuickCard(Icons.AutoMirrored.Filled.Chat, s.chats) { onOpen("chats") }
                QuickCard(Icons.Default.AutoStories, s.status) { onOpen("status") }
                QuickCard(Icons.Default.Mic, s.rooms) { onOpen("rooms") }
                QuickCard(Icons.Default.Groups, s.people) { onOpen("people") }
                QuickCard(Icons.Default.Psychology, s.ask) { onOpen("ask") }
            }
        }
        item {
            Text(
                s.presence,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
            if (live.isEmpty()) {
                Text(
                    s.emptyPeople,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(live, key = { it.id }) { p ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(72.dp)
                                .clickable { onOpen("user/${p.id}") },
                        ) {
                            Avatar(p.displayName, p.photoUrl, size = 58.dp, online = p.online)
                            Text(p.displayName, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
        item {
            Text(
                s.status,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp),
            )
        }
        if (stories.isEmpty()) {
            item {
                Text(s.emptyStatus, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(stories.take(8), key = { it.id }) { st ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onOpen("status") }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(st.userName, st.userPhoto, size = 46.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(st.userName, fontWeight = FontWeight.SemiBold)
                        Text(
                            st.text.ifBlank { if (st.mediaType.startsWith("video")) "Video" else "Photo" },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Text(prettyTime(st.createdAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
        item {
            Text(
                s.chats,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp),
            )
        }
        if (convos.isEmpty()) {
            item {
                Text(s.emptyChats, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(convos.take(6), key = { it.conversationId }) { c ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onOpen("chat/${c.peerId}") }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(c.peerName, c.peerPhoto, online = c.online)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(c.peerName, fontWeight = FontWeight.SemiBold)
                        Text(c.lastText, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(prettyTime(c.lastAt), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        item {
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(20.dp),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(s.worldWide, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(s.realNetwork, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.height(8.dp))
                    Text("${s.developer} · ${s.madeIn}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
private fun QuickCard(icon: ImageVector, label: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            Modifier.padding(horizontal = 16.dp, vertical = 14.dp).width(72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, maxLines = 1)
        }
    }
}
