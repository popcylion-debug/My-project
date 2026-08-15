package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agon.app.data.AppLang
import com.agon.app.data.MqttBus
import com.agon.app.ui.components.LinkPill
import com.agon.app.ui.components.SectionLabel
import com.agon.app.ui.components.hapticTick
import com.agon.app.ui.theme.SalonPalette
import com.agon.app.ui.theme.swatch
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: AppViewModel, onOpenProfile: () -> Unit) {
    val s by vm.strings.collectAsState()
    val settings by vm.settings.collectAsState()
    val me by vm.me.collectAsState()
    val link by vm.link.collectAsState()
    val context = LocalContext.current

    Scaffold(topBar = { TopAppBar(title = { Text(s.settings) }) }) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            LinkPill(vm.linkLabel(), link == MqttBus.Link.Live)
            Spacer(Modifier.height(12.dp))
            SectionLabel(s.language)
            AppLang.entries.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { lang ->
                        FilterChip(
                            selected = settings.language == lang.code,
                            onClick = {
                                hapticTick(context, settings.haptic)
                                vm.setLanguage(lang.code)
                            },
                            label = { Text(lang.nativeName) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            SectionLabel(s.theme)
            SalonPalette.entries.forEach { pal ->
                val sw = pal.swatch()
                val selected = settings.palette == pal.name
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            if (selected) 2.dp else 1.dp,
                            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(0.3f),
                            RoundedCornerShape(16.dp),
                        )
                        .clickable {
                            hapticTick(context, settings.haptic)
                            vm.setPalette(pal.name)
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(sw.a, sw.b, sw.c))),
                    )
                    Spacer(Modifier.size(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(pal.label, fontWeight = FontWeight.SemiBold)
                        Text(pal.story, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            SectionLabel(s.darkMode)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("system" to s.system, "light" to s.light, "dark" to s.dark).forEach { (key, label) ->
                    FilterChip(
                        selected = settings.darkMode == key,
                        onClick = { vm.setDarkMode(key) },
                        label = { Text(label) },
                    )
                }
            }
            SectionLabel("Feel")
            SettingSwitch(s.haptic, s.hapticBody, settings.haptic) {
                hapticTick(context, true, true)
                vm.setHaptic(it)
            }
            SettingSwitch(s.notifications, "Keep the house loud when a message lands.", settings.notifyMessages) {
                vm.setNotify(it)
            }
            SettingSwitch(s.autoplay, "Play incoming voice notes without tapping.", settings.autoplayVoice) {
                vm.setAutoplay(it)
            }
            SectionLabel(s.about)
            Text(s.aboutBody, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(s.developer, fontWeight = FontWeight.Bold)
            Text(s.madeIn, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Salon Na We Yon · ${s.worldWide}", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onOpenProfile, modifier = Modifier.fillMaxWidth()) { Text(s.profile) }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { vm.signOut() },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(s.logout + if (me != null) " · @${me!!.handle}" else "") }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingSwitch(title: String, body: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
