package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LauncherViewModel
import com.example.ui.components.DotSeparator
import com.example.ui.components.NothingCard
import com.example.ui.theme.DotlineCardDark
import com.example.ui.theme.DotlineOutlineDark
import com.example.ui.theme.DotlineRed
import com.example.ui.theme.DotlineTextPrimaryDark
import com.example.ui.theme.DotlineTextSecondaryDark
import com.example.ui.theme.DotoFontFamily
import com.example.ui.theme.SpaceMonoFontFamily

@Composable
fun SetupGuideScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    BackHandler {
        viewModel.navigateTo("settings")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DotlineCardDark)
                        .border(1.dp, DotlineOutlineDark, CircleShape)
                        .clickable { viewModel.navigateTo("settings") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "FINISH THE LOOK",
                    color = DotlineTextPrimaryDark,
                    fontFamily = DotoFontFamily,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Intro explanation
            NothingCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = DotlineRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "To achieve an authentic Nothing OS 5.0 experience on your Samsung Galaxy (One UI), follow these quick steps. Deep links will jump straight into Samsung Settings.",
                        color = DotlineTextSecondaryDark,
                        fontFamily = SpaceMonoFontFamily,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 1: System Dark Mode
            GuideStepCard(
                step = "01",
                title = "Turn on System Dark Mode",
                description = "Forces pure OLED black throughout system dialogs and Samsung apps.",
                buttonLabel = "Display Settings",
                onClick = {
                    openSettings(context, Settings.ACTION_DISPLAY_SETTINGS)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Step 2: Turn off Color Palette
            GuideStepCard(
                step = "02",
                title = "Turn off Material You Palette",
                description = "In Samsung Wallpaper and style, disable 'Color palette' so system elements stay monochrome.",
                buttonLabel = "Wallpaper & Style",
                onClick = {
                    openSettings(context, Intent.ACTION_SET_WALLPAPER)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Step 3: Lock Screen Clock Style
            GuideStepCard(
                step = "03",
                title = "Configure Lock Screen Clock",
                description = "Set Samsung lock screen clock to white / monochrome or digital dot style.",
                buttonLabel = "Lock Screen Settings",
                onClick = {
                    openSettings(context, Settings.ACTION_SECURITY_SETTINGS)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Step 4: 0.5x Animation Scale
            GuideStepCard(
                step = "04",
                title = "Snappy 0.5x Animations",
                description = "In Developer options, set Window, Transition, and Animator scales to 0.5x for Nothing OS snappiness.",
                buttonLabel = "Developer Options",
                onClick = {
                    openSettings(context, Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Step 5: Disable Edge Panels
            GuideStepCard(
                step = "05",
                title = "Turn off Edge Panels & Side Bar",
                description = "Settings > Display > Edge panels > Toggle OFF to avoid gesture conflicts with edge drawers.",
                buttonLabel = "Display Settings",
                onClick = {
                    openSettings(context, Settings.ACTION_DISPLAY_SETTINGS)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            DotSeparator()
            Spacer(modifier = Modifier.height(20.dp))

            // Honest One UI Note
            Text(
                text = "WHAT SAMSUNG ONE UI ALWAYS CONTROLS",
                color = DotlineRed,
                fontFamily = SpaceMonoFontFamily,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            NothingCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "As a third-party launcher, Dotline completely controls your Home Screen, App Drawer, Widgets, Icons, procedural Wallpapers, and Sound Studio.\n\nHowever, Samsung One UI system architecture strictly reserves:\n• Notification Shade & Quick Settings toggles\n• Samsung Settings app visual styling\n• System gesture transition back into home\n• Samsung lock screen structure\n• Physical Nothing Glyph interface hardware (unavailable on Samsung hardware)",
                        color = DotlineTextSecondaryDark,
                        fontFamily = SpaceMonoFontFamily,
                        fontSize = 11.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GuideStepCard(
    step: String,
    title: String,
    description: String,
    buttonLabel: String,
    onClick: () -> Unit
) {
    NothingCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 13.sp)
                Text(step, color = DotlineRed, fontFamily = DotoFontFamily, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(description, color = DotlineTextSecondaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 10.5.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF181818))
                    .clickable(onClick = onClick)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(buttonLabel, color = Color.White, fontFamily = SpaceMonoFontFamily, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ChevronRight, null, tint = DotlineRed, modifier = Modifier.size(14.dp))
            }
        }
    }
}

private fun openSettings(context: Context, action: String) {
    try {
        val intent = Intent(action).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            context.startActivity(Intent(Settings.ACTION_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
        } catch (_: Exception) {}
    }
}
