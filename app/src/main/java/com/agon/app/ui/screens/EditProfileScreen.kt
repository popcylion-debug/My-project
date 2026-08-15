package com.agon.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agon.app.data.AppLang
import com.agon.app.data.favoriteDishes
import com.agon.app.data.sierraRegions
import com.agon.app.data.sierraTribes
import com.agon.app.ui.components.Avatar
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(vm: AppViewModel, onBack: () -> Unit) {
    val s by vm.strings.collectAsState()
    val me by vm.me.collectAsState()
    val busy by vm.busy.collectAsState()
    val notice by vm.notice.collectAsState()
    val snack = remember { SnackbarHostState() }
    val person = me ?: return

    var display by remember { mutableStateOf(person.displayName) }
    var phone by remember { mutableStateOf(person.phone) }
    var email by remember { mutableStateOf(person.email) }
    var city by remember { mutableStateOf(person.city) }
    var district by remember { mutableStateOf(person.district) }
    var region by remember { mutableStateOf(person.region) }
    var tribe by remember { mutableStateOf(person.tribe) }
    var language by remember { mutableStateOf(person.language) }
    var bio by remember { mutableStateOf(person.bio) }
    var dish by remember { mutableStateOf(person.favoriteDish) }
    var purpose by remember { mutableStateOf(person.purpose) }

    LaunchedEffect(notice) {
        if (notice == "saved") {
            snack.showSnackbar(s.saved)
            vm.consumeNotice()
        }
    }

    val pick = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) vm.uploadAvatar(uri) {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.editProfile) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
            )
        },
        snackbarHost = { SnackbarHost(snack) },
    ) { pad ->
        Column(
            Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.clickable { pick.launch("image/*") }) {
                Avatar(display, person.photoUrl, 104.dp, true)
                Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.align(Alignment.BottomEnd), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            Text("@${person.handle}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(display, { display = it }, label = { Text(s.displayName) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(phone, { phone = it }, label = { Text(s.phone) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(email, { email = it }, label = { Text(s.email) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(city, { city = it }, label = { Text(s.city) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(district, { district = it }, label = { Text(s.district) }, modifier = Modifier.fillMaxWidth())
            DropdownField(s.region, region, sierraRegions) { region = it }
            DropdownField(s.tribe, tribe, sierraTribes) { tribe = it }
            DropdownField(s.language, language, AppLang.entries.map { it.code }) { language = it }
            DropdownField("Food", dish, favoriteDishes) { dish = it }
            OutlinedTextField(purpose, { purpose = it }, label = { Text("Why I am here") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(bio, { bio = it }, label = { Text(s.bio) }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Spacer(Modifier.height(16.dp))
            if (busy) CircularProgressIndicator()
            Button(
                enabled = !busy,
                onClick = {
                    vm.saveProfile(
                        person.copy(
                            displayName = display,
                            phone = phone,
                            email = email,
                            city = city,
                            district = district,
                            region = region,
                            tribe = tribe,
                            language = language,
                            bio = bio,
                            favoriteDish = dish,
                            purpose = purpose,
                        ),
                    ) { onBack() }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(s.save) }
        }
    }
}
