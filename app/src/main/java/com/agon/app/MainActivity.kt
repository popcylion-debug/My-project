package com.agon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.agon.app.ui.screens.AskScreen
import com.agon.app.ui.screens.AuthScreen
import com.agon.app.ui.screens.ChatThreadScreen
import com.agon.app.ui.screens.ChatsScreen
import com.agon.app.ui.screens.EditProfileScreen
import com.agon.app.ui.screens.HomeScreen
import com.agon.app.ui.screens.PeopleScreen
import com.agon.app.ui.screens.SettingsScreen
import com.agon.app.ui.screens.StatusScreen
import com.agon.app.ui.screens.UserProfileScreen
import com.agon.app.ui.screens.VoiceRoomsScreen
import com.agon.app.ui.screens.WelcomeScreen
import com.agon.app.ui.theme.AgonAppTheme
import com.agon.app.ui.theme.SalonPalette
import com.agon.app.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val vm: AppViewModel = viewModel()
            val settings by vm.settings.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val dark = when (settings.darkMode) {
                "dark" -> true
                "light" -> false
                else -> systemDark
            }
            val palette = runCatching { SalonPalette.valueOf(settings.palette) }
                .getOrDefault(SalonPalette.LEONE_FLAG)
            AgonAppTheme(darkTheme = dark, palette = palette) {
                RootApp(vm)
            }
        }
    }
}

@Composable
fun RootApp(vm: AppViewModel) {
    val settings by vm.settings.collectAsState()
    val me by vm.me.collectAsState()
    LaunchedEffect(Unit) { vm.restoreIfNeeded() }

    when {
        !settings.onboardingDone -> WelcomeScreen(vm) {}
        me == null -> AuthScreen(vm)
        else -> MainShell(vm)
    }
}

private data class Tab(val route: String, val label: (com.agon.app.data.Strings) -> String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val tabs = listOf(
    Tab("home", { it.home }, Icons.Default.Home),
    Tab("chats", { it.chats }, Icons.AutoMirrored.Filled.Chat),
    Tab("status", { it.status }, Icons.Default.AutoStories),
    Tab("people", { it.people }, Icons.Default.People),
    Tab("ask", { it.ask }, Icons.Default.Psychology),
    Tab("settings", { it.settings }, Icons.Default.Settings),
)

@Composable
fun MainShell(vm: AppViewModel) {
    val nav = rememberNavController()
    val s by vm.strings.collectAsState()
    val back by nav.currentBackStackEntryAsState()
    val route = back?.destination?.route.orEmpty()
    val showBar = route in setOf("home", "chats", "status", "people", "ask", "settings")

    Scaffold(
        bottomBar = {
            if (showBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = route == tab.route,
                            onClick = {
                                nav.navigate(tab.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label(s)) },
                            label = { Text(tab.label(s), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            alwaysShowLabel = false,
                        )
                    }
                }
            }
        },
    ) { pad ->
        NavHost(
            navController = nav,
            startDestination = "home",
            modifier = Modifier.padding(pad),
        ) {
            composable("home") {
                HomeScreen(vm) { dest -> nav.navigate(dest) }
            }
            composable("chats") {
                ChatsScreen(
                    vm = vm,
                    onOpenChat = { nav.navigate("chat/$it") },
                    onNew = { nav.navigate("people") },
                )
            }
            composable("status") { StatusScreen(vm) }
            composable("people") { PeopleScreen(vm) { nav.navigate("user/$it") } }
            composable("ask") { AskScreen(vm) }
            composable("settings") {
                SettingsScreen(vm, onOpenProfile = { nav.navigate("me") })
            }
            composable("rooms") { VoiceRoomsScreen(vm) }
            composable("me") {
                val id = vm.me.value?.id.orEmpty()
                UserProfileScreen(
                    vm = vm,
                    userId = id,
                    onBack = { nav.popBackStack() },
                    onChat = {},
                    onEdit = { nav.navigate("edit") },
                )
            }
            composable("edit") { EditProfileScreen(vm) { nav.popBackStack() } }
            composable(
                route = "user/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                UserProfileScreen(
                    vm = vm,
                    userId = id,
                    onBack = { nav.popBackStack() },
                    onChat = { nav.navigate("chat/$it") },
                    onEdit = { nav.navigate("edit") },
                )
            }
            composable(
                route = "chat/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                ChatThreadScreen(
                    vm = vm,
                    peerId = id,
                    onBack = { nav.popBackStack() },
                    onProfile = { nav.navigate("user/$it") },
                )
            }
        }
    }
}

@Suppress("unused")
private fun unusedNav(nav: NavHostController) = nav
