package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.LauncherViewModel
import com.example.ui.screens.AppDrawerScreen
import com.example.ui.screens.CrashLogScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SetupGuideScreen
import com.example.ui.screens.SoundStudioScreen
import com.example.ui.screens.WallpaperStudioScreen
import com.example.ui.theme.DotlineTheme

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val config by viewModel.config.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()

            DotlineTheme(themeMode = config.themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    Crossfade(
                        targetState = currentScreen,
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            "onboarding" -> OnboardingScreen(viewModel = viewModel)
                            "home" -> HomeScreen(viewModel = viewModel)
                            "drawer" -> AppDrawerScreen(viewModel = viewModel)
                            "wallpaper_studio" -> WallpaperStudioScreen(viewModel = viewModel)
                            "sound_studio" -> SoundStudioScreen(viewModel = viewModel)
                            "settings" -> SettingsScreen(viewModel = viewModel)
                            "setup_guide" -> SetupGuideScreen(viewModel = viewModel)
                            "crash_log" -> CrashLogScreen(viewModel = viewModel)
                            else -> HomeScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Home button tapped: Return to home and dismiss all overlays/drawers
        viewModel.closeAllOverlays()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateClock()
        viewModel.refreshWeather()
        viewModel.refreshCalendar()
    }
}
