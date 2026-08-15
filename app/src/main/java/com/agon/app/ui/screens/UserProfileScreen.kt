package com.agon.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agon.app.ui.components.Avatar
import com.agon.app.ui.components.SectionLabel
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    vm: AppViewModel,
    userId: String,
    onBack: () -> Unit,
    onChat: (String) -> Unit,
    onEdit: () -> Unit,
) {
    val s by vm.strings.collectAsState()
    val me by vm.me.collectAsState()
    val people by vm.people.collectAsState()
    val person = vm.person(userId) ?: me
    val mine = person?.id == me?.id

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (mine) s.profile else person?.displayName ?: s.profile) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
            )
        },
    ) { pad ->
        if (person == null) {
            Column(Modifier.fillMaxSize().padding(pad).padding(24.dp)) {
                Text(s.noResults)
            }
            return@Scaffold
        }
        Column(
            Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Avatar(person.displayName, person.photoUrl, 108.dp, person.online)
            Spacer(Modifier.height(12.dp))
            Text(person.displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("@${person.handle}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (person.online) {
                Text(s.onlineNow, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (mine) {
                    Button(onClick = onEdit) { Text(s.editProfile) }
                } else {
                    Button(onClick = { onChat(person.id) }) {
                        Icon(Icons.AutoMirrored.Filled.Chat, null)
                        Text("  ${s.message}")
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            ProfileLine(s.city, person.city)
            ProfileLine(s.district, person.district)
            ProfileLine(s.region, person.region)
            ProfileLine(s.tribe, person.tribe)
            ProfileLine(s.language, person.language)
            ProfileLine("Food", person.favoriteDish)
            ProfileLine("Why I am here", person.purpose)
            if (person.bio.isNotBlank()) {
                SectionLabel(s.bio)
                Text(person.bio, modifier = Modifier.fillMaxWidth())
            }
            if (person.phone.isNotBlank() && mine) ProfileLine(s.phone, person.phone)
            if (person.email.isNotBlank() && mine) ProfileLine(s.email, person.email)
        }
    }
}

@Composable
private fun ProfileLine(label: String, value: String) {
    if (value.isBlank()) return
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
