package com.example.ui.screens

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LauncherViewModel
import com.example.ui.components.DotSeparator
import com.example.ui.components.NothingButton
import com.example.ui.components.NothingCard
import com.example.ui.theme.DotlineCardDark
import com.example.ui.theme.DotlineOutlineDark
import com.example.ui.theme.DotlineRed
import com.example.ui.theme.DotlineTextPrimaryDark
import com.example.ui.theme.DotlineTextSecondaryDark
import com.example.ui.theme.DotoFontFamily
import com.example.ui.theme.SpaceMonoFontFamily
import com.example.wallpaper.WallpaperConfig
import com.example.wallpaper.WallpaperGenerator
import com.example.wallpaper.WallpaperPattern
import kotlinx.coroutines.launch

@Composable
fun WallpaperStudioScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.roundToPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.roundToPx() }

    var currentConfig by remember { mutableStateOf(WallpaperConfig()) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isApplying by remember { mutableStateOf(false) }

    LaunchedEffect(currentConfig) {
        // Generate preview at responsive resolution
        val bmp = WallpaperGenerator.generate(
            width = minOf(720, screenWidthPx),
            height = minOf(1440, screenHeightPx),
            config = currentConfig
        )
        previewBitmap = bmp
    }

    BackHandler {
        viewModel.navigateTo("home")
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
                        .clickable { viewModel.navigateTo("home") },
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
                    text = "WALLPAPER STUDIO",
                    color = DotlineTextPrimaryDark,
                    fontFamily = DotoFontFamily,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF0D0D0D))
                    .border(1.dp, DotlineOutlineDark, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (previewBitmap != null) {
                    Image(
                        bitmap = previewBitmap!!.asImageBitmap(),
                        contentDescription = "Wallpaper Preview",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Presets Row
            Text(
                text = "PRESETS",
                color = DotlineTextSecondaryDark,
                fontFamily = SpaceMonoFontFamily,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(WallpaperGenerator.presets) { preset ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF141414))
                            .border(1.dp, DotlineOutlineDark, RoundedCornerShape(14.dp))
                            .clickable { currentConfig = preset.config }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = preset.name,
                            color = DotlineTextPrimaryDark,
                            fontFamily = SpaceMonoFontFamily,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            DotSeparator()
            Spacer(modifier = Modifier.height(16.dp))

            // Pattern Selection
            Text(
                text = "PATTERN",
                color = DotlineTextSecondaryDark,
                fontFamily = SpaceMonoFontFamily,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(WallpaperPattern.values()) { pattern ->
                    val isSelected = currentConfig.pattern == pattern
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) DotlineRed else Color(0xFF141414))
                            .border(1.dp, if (isSelected) DotlineRed else DotlineOutlineDark, RoundedCornerShape(14.dp))
                            .clickable { currentConfig = currentConfig.copy(pattern = pattern) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = pattern.displayName,
                            color = if (isSelected) Color.White else DotlineTextSecondaryDark,
                            fontFamily = SpaceMonoFontFamily,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Controls: Dot Size
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "DOT RADIUS",
                    color = DotlineTextSecondaryDark,
                    fontFamily = SpaceMonoFontFamily,
                    fontSize = 11.sp
                )
                Text(
                    text = "${currentConfig.dotRadius.toInt()} px",
                    color = DotlineTextPrimaryDark,
                    fontFamily = SpaceMonoFontFamily,
                    fontSize = 11.sp
                )
            }
            Slider(
                value = currentConfig.dotRadius,
                onValueChange = { currentConfig = currentConfig.copy(dotRadius = it) },
                valueRange = 2f..16f,
                colors = SliderDefaults.colors(
                    thumbColor = DotlineRed,
                    activeTrackColor = DotlineRed,
                    inactiveTrackColor = Color(0xFF222222)
                )
            )

            // Controls: Spacing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "GRID SPACING",
                    color = DotlineTextSecondaryDark,
                    fontFamily = SpaceMonoFontFamily,
                    fontSize = 11.sp
                )
                Text(
                    text = "${currentConfig.spacing.toInt()} px",
                    color = DotlineTextPrimaryDark,
                    fontFamily = SpaceMonoFontFamily,
                    fontSize = 11.sp
                )
            }
            Slider(
                value = currentConfig.spacing,
                onValueChange = { currentConfig = currentConfig.copy(spacing = it) },
                valueRange = 16f..48f,
                colors = SliderDefaults.colors(
                    thumbColor = DotlineRed,
                    activeTrackColor = DotlineRed,
                    inactiveTrackColor = Color(0xFF222222)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Apply Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NothingButton(
                    text = "APPLY HOME",
                    onClick = {
                        previewBitmap?.let { bmp ->
                            isApplying = true
                            coroutineScope.launch {
                                val ok = WallpaperGenerator.applyWallpaper(context, bmp, WallpaperManager.FLAG_SYSTEM)
                                isApplying = false
                                Toast.makeText(context, if (ok) "Home wallpaper applied" else "Failed to apply", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    isPrimary = true,
                    enabled = !isApplying
                )
                NothingButton(
                    text = "APPLY BOTH",
                    onClick = {
                        previewBitmap?.let { bmp ->
                            isApplying = true
                            coroutineScope.launch {
                                val ok = WallpaperGenerator.applyWallpaper(
                                    context,
                                    bmp,
                                    WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                                )
                                isApplying = false
                                Toast.makeText(context, if (ok) "Wallpapers applied" else "Failed to apply", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    isPrimary = false,
                    enabled = !isApplying
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
