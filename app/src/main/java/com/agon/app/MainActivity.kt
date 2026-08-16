package com.agon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agon.app.ui.screens.HomeScreen
import com.agon.app.ui.theme.AgonAppTheme
import com.agon.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val themeMode by mainViewModel.currentTheme.collectAsState()
            val isDarkMode by mainViewModel.isDarkMode.collectAsState()

            AgonAppTheme(
                darkTheme = isDarkMode,
                themeMode = themeMode
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(viewModel = mainViewModel)
                }
            }
        }
    }
}
