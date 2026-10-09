package com.example.ui.screens

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LauncherViewModel
import com.example.ui.components.DotPageIndicator
import com.example.ui.components.DotRingBattery
import com.example.ui.components.NothingButton
import com.example.ui.theme.DotlineRed
import com.example.ui.theme.DotlineTextPrimaryDark
import com.example.ui.theme.DotlineTextSecondaryDark
import com.example.ui.theme.DotoFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.SpaceMonoFontFamily

@Composable
fun OnboardingScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(0) }

    val roleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // Returned from default home dialog
        viewModel.finishOnboarding()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Step Indicator
            DotPageIndicator(
                pageCount = 3,
                currentPage = currentStep,
                modifier = Modifier.padding(top = 16.dp),
                activeColor = DotlineRed
            )

            // Step Content
            AnimatedContent(targetState = currentStep, label = "onboarding_step") { step ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    when (step) {
                        0 -> {
                            // Hero Nothing icon motif
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF101010))
                                    .border(1.5.dp, Color(0xFF2A2A2A), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(DotlineRed)
                                )
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "DOTLINE",
                                color = DotlineTextPrimaryDark,
                                fontFamily = DotoFontFamily,
                                fontSize = 42.sp,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "NOTHING OS 5.0 RECREATION",
                                color = DotlineRed,
                                fontFamily = SpaceMonoFontFamily,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Radical minimalism, raw dot-matrix typography, and zero-distraction design tailored for your Samsung Galaxy.",
                                color = DotlineTextSecondaryDark,
                                fontFamily = SpaceGroteskFontFamily,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        1 -> {
                            DotRingBattery(
                                percentage = 88,
                                isCharging = true,
                                sizeDp = 100.dp,
                                dotCount = 28
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "DOT-MATRIX",
                                color = DotlineTextPrimaryDark,
                                fontFamily = DotoFontFamily,
                                fontSize = 34.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "PURE GEOMETRIC LANGUAGE",
                                color = DotlineTextSecondaryDark,
                                fontFamily = SpaceMonoFontFamily,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Every clock, widget, icon, and tone is procedurally synthesized. No bloatware. No trackers. Just pure essential information.",
                                color = DotlineTextSecondaryDark,
                                fontFamily = SpaceGroteskFontFamily,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        2 -> {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF141414))
                                    .border(1.dp, DotlineRed, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "HOME",
                                    color = Color.White,
                                    fontFamily = SpaceMonoFontFamily,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "MAKE IT YOURS",
                                color = DotlineTextPrimaryDark,
                                fontFamily = DotoFontFamily,
                                fontSize = 34.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "SET AS DEFAULT LAUNCHER",
                                color = DotlineRed,
                                fontFamily = SpaceMonoFontFamily,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Set Dotline as your default home app to replace One UI Home. You can easily switch back anytime in Settings.",
                                color = DotlineTextSecondaryDark,
                                fontFamily = SpaceGroteskFontFamily,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Action Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    NothingButton(
                        text = "BACK",
                        onClick = { currentStep-- },
                        isPrimary = false
                    )
                } else {
                    Spacer(modifier = Modifier.size(1.dp))
                }

                NothingButton(
                    text = if (currentStep == 2) "SET DEFAULT & ENTER" else "NEXT",
                    onClick = {
                        if (currentStep < 2) {
                            currentStep++
                        } else {
                            requestDefaultHome(context, roleLauncher) {
                                viewModel.finishOnboarding()
                            }
                        }
                    },
                    isPrimary = true
                )
            }
        }
    }
}

private fun requestDefaultHome(
    context: Context,
    launcher: androidx.activity.result.ActivityResultLauncher<Intent>,
    onFallback: () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val roleManager = context.getSystemService(Context.ROLE_SERVICE) as? RoleManager
        if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
            if (!roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                launcher.launch(intent)
                return
            }
        }
    }

    try {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
    onFallback()
}
