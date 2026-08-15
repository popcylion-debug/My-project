package com.agon.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.agon.app.R
import com.agon.app.data.sierraRegions
import com.agon.app.data.sierraTribes
import com.agon.app.ui.components.FlagStripe
import com.agon.app.ui.components.hapticTick
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(vm: AppViewModel) {
    val s by vm.strings.collectAsState()
    val settings by vm.settings.collectAsState()
    val answers by vm.answers.collectAsState()
    val busy by vm.busy.collectAsState()
    val notice by vm.notice.collectAsState()
    val context = LocalContext.current

    var loginMode by remember { mutableStateOf(false) }
    var handle by remember { mutableStateOf("") }
    var display by remember { mutableStateOf(answers.displayName) }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var region by remember { mutableStateOf(answers.region) }
    var tribe by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(Modifier.fillMaxWidth().height(180.dp)) {
            Image(
                painterResource(R.drawable.img_community),
                null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.15f), MaterialTheme.colorScheme.background)),
                ),
            )
            Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                Text(s.appName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color.White)
                Text(s.tagline, color = Color.White.copy(alpha = 0.9f))
            }
        }
        FlagStripe()
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(if (loginMode) s.signIn else s.createAccount, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(handle, { handle = it.lowercase().replace(" ", "") }, label = { Text(s.handle) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            if (!loginMode) {
                OutlinedTextField(display, { display = it }, label = { Text(s.displayName) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
            OutlinedTextField(
                password,
                { password = it },
                label = { Text(s.password) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
            AnimatedVisibility(!loginMode) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(phone, { phone = it }, label = { Text(s.phone) }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    OutlinedTextField(email, { email = it }, label = { Text(s.email) }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                    OutlinedTextField(city, { city = it }, label = { Text(s.city) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(district, { district = it }, label = { Text(s.district) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    DropdownField(s.region, region, sierraRegions) { region = it }
                    DropdownField(s.tribe, tribe, sierraTribes) { tribe = it }
                    OutlinedTextField(bio, { bio = it }, label = { Text(s.bio) }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                }
            }
            val shown = error ?: when (notice) {
                "taken" -> s.handleTaken
                "bad" -> s.badLogin
                "short" -> s.passwordShort
                else -> notice
            }
            if (!shown.isNullOrBlank()) {
                Text(shown, color = MaterialTheme.colorScheme.error)
            }
            if (busy) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            Button(
                enabled = !busy,
                onClick = {
                    hapticTick(context, settings.haptic, strong = true)
                    error = null
                    vm.consumeNotice()
                    if (handle.length < 3) {
                        error = s.required
                        return@Button
                    }
                    if (password.length < 6) {
                        error = s.passwordShort
                        return@Button
                    }
                    if (loginMode) {
                        vm.login(handle, password) { ok ->
                            if (!ok) error = s.badLogin
                        }
                    } else {
                        vm.register(
                            handle = handle,
                            displayName = display,
                            password = password,
                            phone = phone,
                            email = email,
                            city = city,
                            district = district,
                            region = region,
                            tribe = tribe,
                            language = answers.language.ifBlank { settings.language },
                            bio = bio,
                            favoriteDish = answers.favoriteDish,
                            purpose = answers.purpose,
                        ) { ok -> if (!ok && error == null) error = s.error }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(if (loginMode) s.signIn else s.register) }
            TextButton(onClick = { loginMode = !loginMode; error = null }) {
                Text(if (loginMode) s.needAccount else s.alreadyHave)
            }
            Text(
                "${s.developer} · ${s.madeIn}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(label: String, value: String, options: List<String>, onPick: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = open, onExpandedChange = { open = it }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(open) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onPick(opt)
                        open = false
                    },
                )
            }
        }
    }
}
