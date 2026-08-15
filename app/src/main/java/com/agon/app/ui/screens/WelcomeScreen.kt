package com.agon.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agon.app.R
import com.agon.app.data.AppLang
import com.agon.app.data.favoriteDishes
import com.agon.app.data.sierraRegions
import com.agon.app.ui.components.FlagStripe
import com.agon.app.ui.components.hapticTick
import com.agon.app.ui.theme.LocalSalonPalette
import com.agon.app.ui.theme.swatch
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WelcomeScreen(vm: AppViewModel, onFinished: () -> Unit) {
    val s by vm.strings.collectAsState()
    val settings by vm.settings.collectAsState()
    val answers by vm.answers.collectAsState()
    val context = LocalContext.current
    var step by remember { mutableIntStateOf(0) }
    val swatch = LocalSalonPalette.current.swatch()

    fun goNext() {
        hapticTick(context, settings.haptic)
        if (step < 5) step++ else {
            vm.finishOnboarding()
            onFinished()
        }
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(Modifier.fillMaxWidth().height(210.dp)) {
            Image(
                painterResource(R.drawable.img_freetown),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                    ),
                ),
            )
            Column(
                Modifier.align(Alignment.BottomStart).padding(20.dp),
            ) {
                Text("SALON NA WE YON", color = Color.White, fontWeight = FontWeight.Black)
                Text(s.welcomeTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        FlagStripe()
        LinearProgressIndicator(
            progress = { (step + 1) / 6f },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        )
        AnimatedContent(step, label = "welcome", modifier = Modifier.weight(1f)) { current ->
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                when (current) {
                    0 -> {
                        Text(s.welcomeBody, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(16.dp))
                        Text("Developed by Henry Tucker · Bo City, Sierra Leone", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    1 -> {
                        Text(s.q1, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = answers.displayName,
                            onValueChange = { vm.setAnswer { a -> a.copy(displayName = it) } },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text(s.q1Hint) },
                        )
                    }
                    2 -> {
                        Text(s.q2, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            sierraRegions.forEach { region ->
                                ChoiceChip(region, answers.region == region) {
                                    hapticTick(context, settings.haptic)
                                    vm.setAnswer { a -> a.copy(region = region) }
                                }
                            }
                        }
                    }
                    3 -> {
                        Text(s.q3, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppLang.entries.forEach { lang ->
                                ChoiceChip("${lang.nativeName} · ${lang.englishName}", answers.language == lang.code) {
                                    hapticTick(context, settings.haptic)
                                    vm.setAnswer { a -> a.copy(language = lang.code) }
                                    vm.setLanguage(lang.code)
                                }
                            }
                        }
                    }
                    4 -> {
                        Text(s.q4, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            favoriteDishes.forEach { dish ->
                                ChoiceChip(dish, answers.favoriteDish == dish) {
                                    hapticTick(context, settings.haptic)
                                    vm.setAnswer { a -> a.copy(favoriteDish = dish) }
                                }
                            }
                        }
                    }
                    5 -> {
                        Text(s.q5, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        val purposes = listOf(s.purposeChat, s.purposeLearn, s.purposeBusiness, s.purposeFamily, s.purposeNews)
                        purposes.forEach { p ->
                            ChoiceChip(p, answers.purpose == p, full = true) {
                                hapticTick(context, settings.haptic)
                                vm.setAnswer { a -> a.copy(purpose = p) }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (step > 0) {
                FilledTonalButton(onClick = { step-- }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    Text(s.back)
                }
            } else {
                Spacer(Modifier.size(8.dp))
            }
            Button(onClick = { goNext() }) {
                Text(if (step == 5) s.finish else s.next)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
            }
        }
        Box(
            Modifier
                .padding(bottom = 16.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(swatch.a)
                .align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun ChoiceChip(label: String, selected: Boolean, full: Boolean = false, onClick: () -> Unit) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg,
        modifier = Modifier
            .then(if (full) Modifier.fillMaxWidth() else Modifier)
            .border(
                1.dp,
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onClick),
    ) {
        Text(label, color = fg, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp))
    }
}
