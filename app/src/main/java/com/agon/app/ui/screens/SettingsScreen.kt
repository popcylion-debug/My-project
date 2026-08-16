package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.AccountTier
import com.agon.app.data.models.SaloneLanguage
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold
import com.agon.app.ui.theme.SaloneThemeMode
import com.agon.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onOpenSecurity: () -> Unit,
    onOpenAdmin: () -> Unit,
    onOpenPremium: () -> Unit,
    onOpenBusiness: () -> Unit,
    onOpenAiChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()
    val accountTier by viewModel.accountTier.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember(currentUser) { mutableStateOf(currentUser.displayName) }
    var editUsername by remember(currentUser) { mutableStateOf(currentUser.username) }
    var editBio by remember(currentUser) { mutableStateOf(currentUser.bio) }
    var editPhone by remember(currentUser) { mutableStateOf(currentUser.phoneNumber) }
    var editEmail by remember(currentUser) { mutableStateOf(currentUser.email) }
    var editTribe by remember(currentUser) { mutableStateOf(currentUser.tribeOrLocation) }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // PROFILE HEADER CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarView(
                        name = currentUser.displayName,
                        avatarUrl = currentUser.avatarUrl,
                        colorHex = currentUser.avatarColorHex,
                        size = 64.dp,
                        isVerified = currentUser.isVerified,
                        isVip = currentUser.isVip
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(currentUser.displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            if (currentUser.isVip) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = SaloneGold
                                ) {
                                    Text("VIP", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                        }
                        Text("@${currentUser.username}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(currentUser.tribeOrLocation, fontSize = 11.sp, color = SaloneEmeraldPrimary, fontWeight = FontWeight.Medium)
                    }

                    IconButton(onClick = { showEditProfileDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = SaloneEmeraldPrimary)
                    }
                }
            }
        }

        // VIP / PREMIUM UPGRADE BANNER
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SaloneGold.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { onOpenPremium() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(SaloneGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Salon Na We Yon VIP & Business Pro", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("4GB file limits, exclusive Salone themes, AI translation & verified gold crest", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SaloneGold)
                }
            }
        }

        // QUICK HUBS: BUSINESS & ADMIN & AI
        item {
            Text(
                text = "Services & Command Centers",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 6.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("KrioGPT & Salone AI Companion 🇸🇱") },
                        supportingContent = { Text("Instant translation, chat summarizer & cultural advice") },
                        leadingContent = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SaloneEmeraldPrimary) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { onOpenAiChat() }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Business Hub & Customer Chat") },
                        supportingContent = { Text("Catalog, quick replies, CRM labels & analytics") },
                        leadingContent = { Icon(Icons.Default.Storefront, contentDescription = null, tint = SaloneEmeraldPrimary) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { onOpenBusiness() }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Admin Dashboard & Moderation") },
                        supportingContent = { Text("248K users metrics, ban management, announcements") },
                        leadingContent = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFFE11D48)) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { onOpenAdmin() }
                    )
                }
            }
        }

        // PREFERENCES SECTION
        item {
            Text(
                text = "App Settings & Customization",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 6.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Privacy & Security") },
                        supportingContent = { Text("App lock, 2FA, biometric, blocked users") },
                        leadingContent = { Icon(Icons.Outlined.Security, contentDescription = null) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { onOpenSecurity() }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Language & Sierra Leone Dialects") },
                        supportingContent = { Text("${currentLang.displayName} (${currentLang.nativeName})") },
                        leadingContent = { Icon(Icons.Outlined.Translate, contentDescription = null) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { showLanguageDialog = true }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Theme & Colors") },
                        supportingContent = { Text(currentTheme.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                        leadingContent = { Icon(Icons.Outlined.Palette, contentDescription = null) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { showThemeDialog = true }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Dark Mode") },
                        leadingContent = { Icon(Icons.Outlined.DarkMode, contentDescription = null) },
                        trailingContent = {
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { viewModel.repository.toggleDarkMode(it) }
                            )
                        }
                    )
                }
            }
        }

        // ACCOUNT ACTIONS
        item {
            Text(
                text = "Account",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 6.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Log Out") },
                        leadingContent = { Icon(Icons.Default.Logout, contentDescription = null, tint = SaloneEmeraldPrimary) },
                        modifier = Modifier.clickable { showLogoutDialog = true }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Delete Account", color = MaterialTheme.colorScheme.error) },
                        leadingContent = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        modifier = Modifier.clickable { showDeleteAccountDialog = true }
                    )
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Salone Profile 🇸🇱") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editUsername,
                        onValueChange = { editUsername = it },
                        label = { Text("Username (@handle)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio / About") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone (+232 ...)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editTribe,
                        onValueChange = { editTribe = it },
                        label = { Text("Region / Community") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.repository.updateProfile(
                            name = editName,
                            username = editUsername,
                            bio = editBio,
                            phone = editPhone,
                            email = editEmail,
                            avatarColor = currentUser.avatarColorHex,
                            tribe = editTribe
                        )
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Language Selector Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Sierra Leone Dialect 🇸🇱") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(SaloneLanguage.entries) { lang ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.repository.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = currentLang == lang,
                                onClick = {
                                    viewModel.repository.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(lang.displayName, fontWeight = FontWeight.Bold)
                                Text("${lang.nativeName} • ${lang.region}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Theme Selector Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Salone Theme 🎨") },
            text = {
                Column {
                    SaloneThemeMode.entries.forEach { theme ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.repository.setTheme(theme)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = currentTheme == theme,
                                onClick = {
                                    viewModel.repository.setTheme(theme)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(theme.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out from Salon Na We Yon?") },
            text = { Text("You can sign back in anytime with your phone or email.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.repository.logout()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Permanently Delete Account? ⚠️") },
            text = { Text("This will remove all your messages, contacts, stories and settings from the Sierra Leone network. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.repository.deleteAccount()
                        showDeleteAccountDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
