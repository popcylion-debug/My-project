package com.agon.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.agon.app.data.models.Message
import com.agon.app.data.models.MessageType
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold

@Composable
fun MediaPreviewDialog(
    message: Message,
    onDismiss: () -> Unit,
    onDownload: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
        ) {
            // Top Bar Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(message.senderName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(message.timeFormatted, color = Color.LightGray, fontSize = 11.sp)
                }
                IconButton(onClick = onDownload) {
                    Icon(Icons.Default.Download, contentDescription = "Download", tint = Color.White)
                }
            }

            // Center Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 90.dp),
                contentAlignment = Alignment.Center
            ) {
                when (message.type) {
                    MessageType.IMAGE -> {
                        if (message.mediaUrl != null) {
                            AsyncImage(
                                model = message.mediaUrl,
                                contentDescription = "Image preview",
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = SaloneEmeraldPrimary, modifier = Modifier.size(80.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("High-Resolution Salone Image", color = Color.White)
                            }
                        }
                    }

                    MessageType.VIDEO -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(SaloneEmeraldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play Video", tint = Color.White, modifier = Modifier.size(54.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Full HD Video Player", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("04:32 • 1080p • 60 FPS", color = Color.LightGray, fontSize = 12.sp)
                        }
                    }

                    MessageType.DOCUMENT -> {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(message.documentData?.fileName ?: "Sierra_Leone_Document.pdf", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("${message.documentData?.fileSizeText ?: "3.8 MB"} • Encrypted Document", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = onDownload,
                                    colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Download to Device")
                                }
                            }
                        }
                    }

                    else -> {
                        Text(message.text, color = Color.White, fontSize = 18.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }
        }
    }
}
