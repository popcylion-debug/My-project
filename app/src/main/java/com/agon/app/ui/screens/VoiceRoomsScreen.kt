package com.agon.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.agon.app.data.VoiceRoom
import com.agon.app.ui.components.EmptyState
import com.agon.app.ui.components.VoiceBubble
import com.agon.app.ui.components.VoiceRecorder
import com.agon.app.ui.components.hapticTick
import com.agon.app.ui.components.prettyTime
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceRoomsScreen(vm: AppViewModel) {
    val s by vm.strings.collectAsState()
    val settings by vm.settings.collectAsState()
    val rooms by vm.rooms.collectAsState()
    val clips by vm.clips.collectAsState()
    val context = LocalContext.current
    var creating by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var open by remember { mutableStateOf<VoiceRoom?>(null) }
    val recorder = remember { VoiceRecorder(context) }
    var recording by remember { mutableStateOf(false) }

    val recPerm = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) {
            runCatching {
                recorder.start()
                recording = true
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(s.rooms) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { creating = true }) {
                Icon(Icons.Default.Add, s.newRoom)
            }
        },
    ) { pad ->
        if (rooms.isEmpty()) {
            EmptyState(s.rooms, "Open a room and talk. Voice notes land for everyone in the house.", Modifier.fillMaxSize().padding(pad))
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(pad)) {
                items(rooms, key = { it.id }) { room ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { open = room },
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(room.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            if (room.topic.isNotBlank()) Text(room.topic, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${room.hostName} · ${prettyTime(room.createdAt)} · ${clips.count { it.roomId == room.id }} clips",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }
        }
    }

    if (creating) {
        ModalBottomSheet(onDismissRequest = { creating = false }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
            Column(Modifier.padding(20.dp)) {
                Text(s.newRoom, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(title, { title = it }, label = { Text(s.roomTitle) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(topic, { topic = it }, label = { Text(s.roomTopic) }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        vm.createRoom(title, topic)
                        title = ""
                        topic = ""
                        creating = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(s.post) }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    open?.let { room ->
        ModalBottomSheet(onDismissRequest = { open = null }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
            val roomClips = clips.filter { it.roomId == room.id }
            Column(Modifier.fillMaxWidth().padding(16.dp).height(480.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(room.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(room.topic.ifBlank { room.hostName }, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { open = null }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.weight(1f)) {
                    items(roomClips, key = { it.id }) { clip ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, null)
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(clip.senderName, fontWeight = FontWeight.SemiBold)
                                VoiceBubble(clip.mediaUrl, clip.durationMs, false)
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        if (recording) {
                            val result = recorder.stop()
                            recording = false
                            if (result != null && result.second > 400) vm.sendRoomClip(room, result.first, result.second)
                        } else {
                            hapticTick(context, settings.haptic, true)
                            val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                            if (!granted) recPerm.launch(Manifest.permission.RECORD_AUDIO)
                            else {
                                runCatching {
                                    recorder.start()
                                    recording = true
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Mic, null)
                    Text(if (recording) "  Stop & send" else "  ${s.speak}")
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
