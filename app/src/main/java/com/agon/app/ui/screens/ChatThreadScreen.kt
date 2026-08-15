package com.agon.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.agon.app.data.conversationIdFor
import com.agon.app.ui.components.Avatar
import com.agon.app.ui.components.RecordingBar
import com.agon.app.ui.components.VoiceBubble
import com.agon.app.ui.components.VoiceRecorder
import com.agon.app.ui.components.clockTime
import com.agon.app.ui.components.hapticTick
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatThreadScreen(vm: AppViewModel, peerId: String, onBack: () -> Unit, onProfile: (String) -> Unit) {
    val s by vm.strings.collectAsState()
    val settings by vm.settings.collectAsState()
    val me by vm.me.collectAsState()
    val messages by vm.messages.collectAsState()
    val people by vm.people.collectAsState()
    val busy by vm.busy.collectAsState()
    val context = LocalContext.current
    val peer = vm.person(peerId)
    val myId = me?.id.orEmpty()
    val cid = if (me != null) conversationIdFor(myId, peerId) else peerId
    val thread = vm.thread(cid)
    var draft by remember { mutableStateOf("") }
    var menu by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    val recorder = remember { VoiceRecorder(context) }
    val listState = rememberLazyListState()

    LaunchedEffect(thread.size) {
        if (thread.isNotEmpty()) listState.animateScrollToItem(thread.lastIndex)
    }

    val photoPick = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null && peer != null) vm.sendMedia(peer, uri, "image")
    }
    val videoPick = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null && peer != null) vm.sendMedia(peer, uri, "video")
    }
    val recPerm = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) {
            runCatching {
                recorder.start()
                recording = true
                hapticTick(context, settings.haptic, true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onProfile(peerId) },
                    ) {
                        Avatar(peer?.displayName ?: "?", peer?.photoUrl.orEmpty(), 36.dp, peer?.online == true)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(peer?.displayName ?: s.people)
                            Text(
                                if (peer?.online == true) s.onlineNow else listOf(peer?.city, peer?.tribe).filter { !it.isNullOrBlank() }.joinToString(" · "),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
            )
        },
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).imePadding()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(thread, key = { it.id }) { msg ->
                    val mine = msg.senderId == myId
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp,
                                bottomEnd = if (mine) 4.dp else 18.dp,
                                bottomStart = if (mine) 18.dp else 4.dp,
                            ),
                            color = if (mine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.widthIn(max = 300.dp),
                        ) {
                            Column(Modifier.padding(10.dp)) {
                                if (msg.mediaType == "image" && msg.mediaUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = msg.mediaUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                    )
                                    Spacer(Modifier.height(6.dp))
                                }
                                if (msg.mediaType == "video" && msg.mediaUrl.isNotBlank()) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.inverseSurface),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.Videocam, null, tint = MaterialTheme.colorScheme.inverseOnSurface)
                                            Text(msg.mediaUrl.takeLast(18), color = MaterialTheme.colorScheme.inverseOnSurface, style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                    Spacer(Modifier.height(6.dp))
                                }
                                if (msg.mediaType == "audio" && msg.mediaUrl.isNotBlank()) {
                                    VoiceBubble(msg.mediaUrl, msg.durationMs, mine)
                                    Spacer(Modifier.height(4.dp))
                                }
                                if (msg.text.isNotBlank()) {
                                    Text(
                                        msg.text,
                                        color = if (mine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                                Text(
                                    clockTime(msg.createdAt),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (mine) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.align(Alignment.End),
                                )
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
            if (busy) {
                Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(s.uploading, style = MaterialTheme.typography.labelMedium)
                }
            }
            if (recording) {
                RecordingBar(recorder.startedAt)
            }
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box {
                    IconButton(onClick = { menu = true }) { Icon(Icons.Default.Add, s.attach) }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        DropdownMenuItem(
                            text = { Text(s.photo) },
                            onClick = { menu = false; photoPick.launch("image/*") },
                            leadingIcon = { Icon(Icons.Default.Image, null) },
                        )
                        DropdownMenuItem(
                            text = { Text(s.video) },
                            onClick = { menu = false; videoPick.launch("video/*") },
                            leadingIcon = { Icon(Icons.Default.Videocam, null) },
                        )
                    }
                }
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(s.typeMessage) },
                    maxLines = 4,
                    shape = RoundedCornerShape(24.dp),
                )
                Spacer(Modifier.width(6.dp))
                if (draft.isBlank()) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (recording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondaryContainer)
                            .pointerInput(peer, recording) {
                                detectTapGestures(
                                    onPress = {
                                        if (peer == null) return@detectTapGestures
                                        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                                        if (!granted) {
                                            recPerm.launch(Manifest.permission.RECORD_AUDIO)
                                            return@detectTapGestures
                                        }
                                        runCatching {
                                            recorder.start()
                                            recording = true
                                            hapticTick(context, settings.haptic, true)
                                        }
                                        val released = tryAwaitRelease()
                                        val result = recorder.stop()
                                        recording = false
                                        if (released && result != null && result.second > 400 && peer != null) {
                                            vm.sendVoice(peer, result.first, result.second)
                                        }
                                    },
                                )
                            },
                    ) {
                        Icon(if (recording) Icons.Default.Close else Icons.Default.Mic, s.voice)
                    }
                } else {
                    IconButton(
                        onClick = {
                            val t = draft.trim()
                            if (t.isNotBlank() && peer != null) {
                                hapticTick(context, settings.haptic)
                                vm.sendText(peer, t)
                                draft = ""
                            }
                        },
                    ) { Icon(Icons.AutoMirrored.Filled.Send, s.send) }
                }
            }
        }
    }
}
