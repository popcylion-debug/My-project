package com.agon.app.ui.components

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.agon.app.data.UserProfile
import com.agon.app.ui.theme.LocalSalonPalette
import com.agon.app.ui.theme.swatch
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun hapticTick(context: Context, enabled: Boolean, strong: Boolean = false) {
    if (!enabled) return
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        val ms = if (strong) 28L else 14L
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(ms)
        }
    } catch (_: Exception) {
    }
}

fun prettyTime(ts: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - ts
    return when {
        diff < 45_000 -> "now"
        diff < 60 * 60_000 -> "${diff / 60_000}m"
        diff < 24 * 60 * 60_000 -> "${diff / 3_600_000}h"
        diff < 48 * 60 * 60_000 -> "yday"
        else -> SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(ts))
    }
}

fun clockTime(ts: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ts))

@Composable
fun Avatar(
    name: String,
    photoUrl: String,
    size: Dp = 48.dp,
    online: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val initials = name.trim().split(" ").filter { it.isNotBlank() }
        .take(2).joinToString("") { it.first().uppercase() }.ifBlank { "?" }
    val swatch = LocalSalonPalette.current.swatch()
    Box(modifier.size(size), contentAlignment = Alignment.BottomEnd) {
        if (photoUrl.startsWith("http")) {
            AsyncImage(
                model = photoUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), CircleShape),
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(swatch.a, swatch.c))),
                contentAlignment = Alignment.Center,
            ) {
                if (initials == "?") {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(size / 2))
                } else {
                    Text(
                        initials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value / 2.6f).sp,
                    )
                }
            }
        }
        if (online) {
            Box(
                Modifier
                    .size(size * 0.28f)
                    .clip(CircleShape)
                    .background(Color(0xFF1EB53A))
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
            )
        }
    }
}

@Composable
fun FlagStripe(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().height(6.dp)) {
        Box(Modifier.weight(1f).height(6.dp).background(Color(0xFF1EB53A)))
        Box(Modifier.weight(1f).height(6.dp).background(Color(0xFFF7F9F6)))
        Box(Modifier.weight(1f).height(6.dp).background(Color(0xFF0072C6)))
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(Locale.getDefault()),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.1.sp,
        modifier = modifier.padding(horizontal = 4.dp, vertical = 8.dp),
    )
}

@Composable
fun EmptyState(title: String, body: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun LinkPill(text: String, live: Boolean) {
    val color by animateColorAsState(
        if (live) Color(0xFF1EB53A) else MaterialTheme.colorScheme.outline,
        label = "link",
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun PersonRow(person: UserProfile, subtitle: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(person.displayName, person.photoUrl, online = person.online)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(person.displayName, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (person.city.isNotBlank()) {
            Text(person.city, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun VoiceBubble(url: String, durationMs: Long, mine: Boolean) {
    val context = LocalContext.current
    var playing by remember { mutableStateOf(false) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }
    DisposableEffect(url) {
        onDispose {
            player?.release()
            player = null
        }
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (mine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.clickable {
            if (playing) {
                player?.pause()
                playing = false
            } else {
                try {
                    if (player == null) {
                        player = MediaPlayer().apply {
                            setDataSource(url)
                            setOnPreparedListener {
                                start()
                                playing = true
                            }
                            setOnCompletionListener { playing = false }
                            prepareAsync()
                        }
                    } else {
                        player?.start()
                        playing = true
                    }
                } catch (_: Exception) {
                }
            }
        },
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = if (mine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                if (durationMs > 0) "${durationMs / 1000}s voice" else "Voice note",
                color = if (mine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

class VoiceRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    var file: File? = null
        private set
    var startedAt: Long = 0L
        private set

    fun start(): File {
        val out = File(context.cacheDir, "rec_${System.currentTimeMillis()}.m4a")
        val rec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        rec.setAudioSource(MediaRecorder.AudioSource.MIC)
        rec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        rec.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        rec.setAudioEncodingBitRate(96_000)
        rec.setAudioSamplingRate(44_100)
        rec.setOutputFile(out.absolutePath)
        rec.prepare()
        rec.start()
        recorder = rec
        file = out
        startedAt = System.currentTimeMillis()
        return out
    }

    fun stop(): Pair<File, Long>? {
        return try {
            recorder?.stop()
            recorder?.release()
            recorder = null
            val f = file ?: return null
            f to (System.currentTimeMillis() - startedAt)
        } catch (_: Exception) {
            recorder?.release()
            recorder = null
            null
        }
    }

    fun cancel() {
        try {
            recorder?.stop()
        } catch (_: Exception) {
        }
        recorder?.release()
        recorder = null
        file?.delete()
        file = null
    }
}

@Composable
fun RecordingBar(startedAt: Long) {
    var elapsed by remember { mutableStateOf(0L) }
    LaunchedEffect(startedAt) {
        while (true) {
            elapsed = System.currentTimeMillis() - startedAt
            delay(200)
        }
    }
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("●  Recording  ${elapsed / 1000}s", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.SemiBold)
        Text("Release to send · slide away to cancel", style = MaterialTheme.typography.labelSmall)
    }
}
