package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.PrivacySettings
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold
import com.agon.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityPrivacyScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val privacySettings by viewModel.privacySettings.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }

    var show2FaDialog by remember { mutableStateOf(false) }
    var twoFaPinInput by remember { mutableStateOf("") }

    var showVisibilityDialog by remember { mutableStateOf<String?>(null) } // "lastSeen", "profilePhoto", "status"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy & Security 🔒", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // End-to-End Encryption Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SaloneEmeraldPrimary.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SaloneEmeraldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("End-to-End Encryption Active 🇸🇱", fontWeight = FontWeight.Bold, color = SaloneEmeraldPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Your personal chats and calls are secured with 256-bit cryptographic keys. No one, including Salon Na We Yon, can decrypt your communications.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Security Key: 8492-1049-5501-7294-0012-9844", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // App Lock & Biometrics
            item {
                Text("App Security", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("App Lock PIN") },
                            supportingContent = { Text(if (privacySettings.appLockEnabled) "PIN protection enabled" else "Require PIN on launch") },
                            leadingContent = { Icon(Icons.Default.Pin, contentDescription = null) },
                            trailingContent = {
                                Switch(
                                    checked = privacySettings.appLockEnabled,
                                    onCheckedChange = { enabled ->
                                        if (enabled) {
                                            showPinDialog = true
                                        } else {
                                            viewModel.repository.updatePrivacySettings(privacySettings.copy(appLockEnabled = false))
                                        }
                                    }
                                )
                            }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text("Biometric Unlock") },
                            supportingContent = { Text("Use Fingerprint / Face ID to unlock") },
                            leadingContent = { Icon(Icons.Default.Fingerprint, contentDescription = null) },
                            trailingContent = {
                                Switch(
                                    checked = privacySettings.biometricEnabled,
                                    onCheckedChange = {
                                        viewModel.repository.updatePrivacySettings(privacySettings.copy(biometricEnabled = it))
                                    }
                                )
                            }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text("Two-Factor Authentication (2FA)") },
                            supportingContent = { Text(if (privacySettings.twoFactorEnabled) "2FA PIN active" else "Extra security layer for sign-in") },
                            leadingContent = { Icon(Icons.Default.Password, contentDescription = null) },
                            trailingContent = {
                                Switch(
                                    checked = privacySettings.twoFactorEnabled,
                                    onCheckedChange = { enabled ->
                                        if (enabled) {
                                            show2FaDialog = true
                                        } else {
                                            viewModel.repository.updatePrivacySettings(privacySettings.copy(twoFactorEnabled = false))
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            }

            // Privacy Controls
            item {
                Text("Privacy Controls", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Last Seen & Online") },
                            supportingContent = { Text(privacySettings.lastSeenVisibility) },
                            leadingContent = { Icon(Icons.Default.Visibility, contentDescription = null) },
                            modifier = Modifier.clickable { showVisibilityDialog = "lastSeen" }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text("Profile Photo") },
                            supportingContent = { Text(privacySettings.profilePhotoVisibility) },
                            leadingContent = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                            modifier = Modifier.clickable { showVisibilityDialog = "profilePhoto" }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text("Status Updates") },
                            supportingContent = { Text(privacySettings.statusVisibility) },
                            leadingContent = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                            modifier = Modifier.clickable { showVisibilityDialog = "status" }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text("Read Receipts (Blue Ticks)") },
                            supportingContent = { Text("If disabled, you won't see or send read receipts") },
                            leadingContent = { Icon(Icons.Default.DoneAll, contentDescription = null) },
                            trailingContent = {
                                Switch(
                                    checked = privacySettings.readReceiptsEnabled,
                                    onCheckedChange = {
                                        viewModel.repository.updatePrivacySettings(privacySettings.copy(readReceiptsEnabled = it))
                                    }
                                )
                            }
                        )
                    }
                }
            }

            // Blocked Users Management
            item {
                Text("Blocked Accounts (${blockedUsers.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    if (blockedUsers.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("No blocked users. You can block any contact from their profile.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        Column {
                            blockedUsers.forEach { bUser ->
                                ListItem(
                                    headlineContent = { Text(bUser.displayName) },
                                    supportingContent = { Text("@${bUser.username}", fontSize = 11.sp) },
                                    leadingContent = { AvatarView(name = bUser.displayName, size = 32.dp) },
                                    trailingContent = {
                                        Button(
                                            onClick = { viewModel.repository.unblockUser(bUser.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Unblock", fontSize = 11.sp)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // App Lock PIN Setup Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Set 4-Digit App Lock PIN") },
            text = {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { if (it.length <= 4) pinInput = it },
                    label = { Text("Enter 4 digits") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput.length == 4) {
                            viewModel.repository.updatePrivacySettings(privacySettings.copy(appLockEnabled = true, appLockPin = pinInput))
                            showPinDialog = false
                            pinInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary)
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2FA Setup Dialog
    if (show2FaDialog) {
        AlertDialog(
            onDismissRequest = { show2FaDialog = false },
            title = { Text("Enable Two-Factor Authentication 🔐") },
            text = {
                Column {
                    Text("Create a 6-digit PIN that will be required whenever you register your phone number with Salon Na We Yon.", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = twoFaPinInput,
                        onValueChange = { if (it.length <= 6) twoFaPinInput = it },
                        label = { Text("6-Digit 2FA PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (twoFaPinInput.length == 6) {
                            viewModel.repository.updatePrivacySettings(privacySettings.copy(twoFactorEnabled = true, twoFactorPin = twoFaPinInput))
                            show2FaDialog = false
                            twoFaPinInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary)
                ) {
                    Text("Enable 2FA")
                }
            },
            dismissButton = {
                TextButton(onClick = { show2FaDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Visibility Selector Dialog
    if (showVisibilityDialog != null) {
        val target = showVisibilityDialog!!
        AlertDialog(
            onDismissRequest = { showVisibilityDialog = null },
            title = { Text("Who Can See This?") },
            text = {
                Column {
                    listOf("Everyone", "My Contacts", "Nobody").forEach { opt ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (target) {
                                        "lastSeen" -> viewModel.repository.updatePrivacySettings(privacySettings.copy(lastSeenVisibility = opt))
                                        "profilePhoto" -> viewModel.repository.updatePrivacySettings(privacySettings.copy(profilePhotoVisibility = opt))
                                        "status" -> viewModel.repository.updatePrivacySettings(privacySettings.copy(statusVisibility = opt))
                                    }
                                    showVisibilityDialog = null
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = when (target) {
                                    "lastSeen" -> privacySettings.lastSeenVisibility == opt
                                    "profilePhoto" -> privacySettings.profilePhotoVisibility == opt
                                    else -> privacySettings.statusVisibility == opt
                                },
                                onClick = {
                                    when (target) {
                                        "lastSeen" -> viewModel.repository.updatePrivacySettings(privacySettings.copy(lastSeenVisibility = opt))
                                        "profilePhoto" -> viewModel.repository.updatePrivacySettings(privacySettings.copy(profilePhotoVisibility = opt))
                                        "status" -> viewModel.repository.updatePrivacySettings(privacySettings.copy(statusVisibility = opt))
                                    }
                                    showVisibilityDialog = null
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(opt)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVisibilityDialog = null }) {
                    Text("Close")
                }
            }
        )
    }
}
