package com.agon.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.agon.app.ui.components.Avatar
import com.agon.app.ui.components.EmptyState
import com.agon.app.ui.components.hapticTick
import com.agon.app.ui.components.prettyTime
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScreen(vm: AppViewModel) {
    val s by vm.strings.collectAsState()
    val settings by vm.settings.collectAsState()
    val statuses by vm.statuses.collectAsState()
    val busy by vm.busy.collectAsState()
    val context = LocalContext.current
    val stories = vm.liveStatuses()
    var composer by remember { mutableStateOf(false) }
    var viewing by remember { mutableStateOf<com.agon.app.data.StatusUpdate?>(null) }
    var text by remember { mutableStateOf("") }
    var media by remember { mutableStateOf<Uri?>(null) }
    var mediaType by remember { mutableStateOf("") }

    val photoPick = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            media = uri
            mediaType = "image"
        }
    }
    val videoPick = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            media = uri
            mediaType = "video"
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(s.status) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { composer = true }) {
                Icon(Icons.Default.Add, s.addStatus)
            }
        },
    ) { pad ->
        if (stories.isEmpty()) {
            EmptyState(s.status, s.emptyStatus, Modifier.fillMaxSize().padding(pad))
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(pad)) {
                items(stories, key = { it.id }) { st ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { viewing = st }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Avatar(st.userName, st.userPhoto, 52.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(st.userName, style = MaterialTheme.typography.titleMedium)
                            Text(
                                st.text.ifBlank { if (st.mediaType.startsWith("video")) s.video else s.photo },
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                            )
                        }
                        Text(prettyTime(st.createdAt), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }

    if (composer) {
        ModalBottomSheet(onDismissRequest = { composer = false }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
            Column(Modifier.padding(20.dp)) {
                Text(s.addStatus, style = MaterialTheme.typography.titleLarge)
                Text(s.expires, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(text, { text = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text(s.statusHint) }, minLines = 3)
                Spacer(Modifier.height(8.dp))
                Row {
                    IconButton(onClick = { photoPick.launch("image/*") }) { Icon(Icons.Default.Image, s.photo) }
                    IconButton(onClick = { videoPick.launch("video/*") }) { Icon(Icons.Default.Videocam, s.video) }
                    if (media != null) Text(if (mediaType == "video") s.videoPicked else s.photoPicked, modifier = Modifier.align(Alignment.CenterVertically))
                }
                Button(
                    enabled = !busy && (text.isNotBlank() || media != null),
                    onClick = {
                        hapticTick(context, settings.haptic, true)
                        vm.postStatus(text, media, mediaType)
                        text = ""
                        media = null
                        mediaType = ""
                        composer = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(s.post) }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    viewing?.let { st ->
        ModalBottomSheet(onDismissRequest = { viewing = null }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(st.userName, st.userPhoto)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(st.userName, style = MaterialTheme.typography.titleMedium)
                        Text(prettyTime(st.createdAt), style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = { viewing = null }) { Icon(Icons.Default.Close, null) }
                }
                Spacer(Modifier.height(12.dp))
                if (st.mediaUrl.isNotBlank() && st.mediaType == "image") {
                    AsyncImage(
                        model = st.mediaUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(280.dp).clip(RoundedCornerShape(16.dp)),
                    )
                    Spacer(Modifier.height(12.dp))
                }
                if (st.mediaType == "video" && st.mediaUrl.isNotBlank()) {
                    Card(Modifier.fillMaxWidth().height(160.dp)) {
                        Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                            Text(st.mediaUrl, color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                if (st.text.isNotBlank()) Text(st.text, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}
