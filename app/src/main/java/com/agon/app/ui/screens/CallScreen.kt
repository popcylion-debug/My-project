package com.agon.app.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.CallMediaType
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.theme.SaloneAtlanticBlue
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold
import com.agon.app.viewmodel.MainViewModel

@Composable
fun CallScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeCall by viewModel.activeCall.collectAsState()

    if (activeCall == null) return

    val call = activeCall!!
    val isVideo = call.mediaType == CallMediaType.VIDEO

    val minutes = call.durationSeconds / 60
    val seconds = call.durationSeconds % 60
    val durationText = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Video Background / Camera Simulation View
        if (isVideo && !call.isCameraOff) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (call.isFrontCamera) Color(0xFF1E293B) else Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                if (call.isScreenSharing) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(24.dp)
                            .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
                            .padding(24.dp)
                    ) {
                        Icon(Icons.Default.ScreenShare, contentDescription = null, tint = SaloneEmeraldPrimary, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("You are sharing your screen", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Salon Na We Yon HD Screen Cast", color = Color.LightGray, fontSize = 12.sp)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AvatarView(
                            name = call.peerUser.displayName,
                            avatarUrl = call.peerUser.avatarUrl,
                            colorHex = call.peerUser.avatarColorHex,
                            size = 120.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (call.isFrontCamera) "Front Camera Active 📷" else "Rear Camera Active 🎥",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Top Status Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = call.connectionQuality,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!isVideo || call.isCameraOff) {
                AvatarView(
                    name = if (call.isGroupCall) (call.groupName ?: "Group") else call.peerUser.displayName,
                    avatarUrl = call.peerUser.avatarUrl,
                    colorHex = call.peerUser.avatarColorHex,
                    size = 100.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = if (call.isGroupCall) (call.groupName ?: "Group Call") else call.peerUser.displayName,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (call.isRinging) "Ringing in Sierra Leone... 🇸🇱" else durationText,
                color = if (call.isRinging) SaloneGold else Color(0xFF94A3B8),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Bottom Call Controls
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 20.dp, end = 20.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF1E293B).copy(alpha = 0.9f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                // Secondary Controls Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Mic
                    IconButton(
                        onClick = { viewModel.repository.toggleCallMute() },
                        modifier = Modifier
                            .size(50.dp)
                            .background(if (call.isMuted) Color(0xFFE11D48) else Color(0xFF334155), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (call.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = Color.White
                        )
                    }

                    // Toggle Camera
                    if (isVideo) {
                        IconButton(
                            onClick = { viewModel.repository.toggleCallCamera() },
                            modifier = Modifier
                                .size(50.dp)
                                .background(if (call.isCameraOff) Color(0xFFE11D48) else Color(0xFF334155), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (call.isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                contentDescription = "Camera",
                                tint = Color.White
                            )
                        }

                        // Flip Camera
                        IconButton(
                            onClick = { viewModel.repository.flipCamera() },
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFF334155), CircleShape)
                        ) {
                            Icon(Icons.Default.Cameraswitch, contentDescription = "Flip", tint = Color.White)
                        }

                        // Screen Sharing
                        IconButton(
                            onClick = { viewModel.repository.toggleScreenShare() },
                            modifier = Modifier
                                .size(50.dp)
                                .background(if (call.isScreenSharing) SaloneEmeraldPrimary else Color(0xFF334155), CircleShape)
                        ) {
                            Icon(Icons.Default.ScreenShare, contentDescription = "Screen Share", tint = Color.White)
                        }
                    }

                    // Speakerphone
                    IconButton(
                        onClick = { viewModel.repository.toggleCallSpeaker() },
                        modifier = Modifier
                            .size(50.dp)
                            .background(if (call.isSpeakerOn) SaloneAtlanticBlue else Color(0xFF334155), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (call.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                            contentDescription = "Speaker",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // End Call Red Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { viewModel.repository.endCall() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}
