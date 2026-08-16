package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold
import com.agon.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRShareScreen(
    viewModel: MainViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: My Code, 1: Scan Code

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Salone QR Share 🇸🇱", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = SaloneEmeraldPrimary,
                modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("My Salone Code") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Scan QR") }
                )
            }

            if (selectedTab == 0) {
                // MY QR CODE CARD
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(top = 20.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AvatarView(
                            name = currentUser.displayName,
                            avatarUrl = currentUser.avatarUrl,
                            colorHex = currentUser.avatarColorHex,
                            size = 64.dp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(currentUser.displayName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("@${currentUser.username} • ${currentUser.phoneNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(20.dp))

                        // QR Simulated Grid
                        Surface(
                            modifier = Modifier
                                .size(180.dp)
                                .border(2.dp, SaloneEmeraldPrimary, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.QrCode2, contentDescription = "QR Code", tint = Color.Black, modifier = Modifier.size(150.dp))
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SaloneEmeraldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🇸🇱", fontSize = 18.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your QR code is private. Sierra Leone friends can scan this code to add you instantly on Salon Na We Yon.",
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // SCANNER SIMULATION VIEW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .border(3.dp, SaloneEmeraldPrimary, RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CenterFocusStrong, contentDescription = null, tint = SaloneEmeraldPrimary, modifier = Modifier.size(90.dp))
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Point camera at friend's Salon Na We Yon QR code", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val userToScan = allUsers.firstOrNull { it.id == "user_aminata" } ?: allUsers.first()
                            val chatId = viewModel.repository.getOrCreateDirectChat(userToScan)
                            viewModel.openChat(chatId)
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simulate Scan (Connect with Aminata)")
                    }
                }
            }
        }
    }
}
