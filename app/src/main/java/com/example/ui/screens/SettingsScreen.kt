package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LauncherViewModel
import com.example.ui.components.DotSeparator
import com.example.ui.components.NothingButton
import com.example.ui.components.NothingCard
import com.example.ui.components.NothingSwitch
import com.example.ui.theme.DotlineCardDark
import com.example.ui.theme.DotlineOutlineDark
import com.example.ui.theme.DotlineRed
import com.example.ui.theme.DotlineTextPrimaryDark
import com.example.ui.theme.DotlineTextSecondaryDark
import com.example.ui.theme.DotoFontFamily
import com.example.ui.theme.SpaceMonoFontFamily

@Composable
fun SettingsScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val config by viewModel.config.collectAsState()
    val context = LocalContext.current

    var showBackupDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importText by remember { mutableStateOf("") }

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
                    text = "SETTINGS",
                    color = DotlineTextPrimaryDark,
                    fontFamily = DotoFontFamily,
                    fontSize = 24.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Home Grid & Layout
            SettingsSectionHeader(title = "HOME GRID & LAYOUT")
            NothingCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Grid Columns", color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(4, 5, 6).forEach { cols ->
                                val isSelected = config.gridCols == cols
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) DotlineRed else Color(0xFF1E1E1E))
                                        .clickable { viewModel.updateConfig(config.copy(gridCols = cols)) }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("$cols", color = Color.White, fontFamily = SpaceMonoFontFamily, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    DotSeparator()
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Infinite Page Scroll", color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp)
                        NothingSwitch(
                            checked = config.infiniteScroll,
                            onCheckedChange = { viewModel.updateConfig(config.copy(infiniteScroll = it)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Icons
            SettingsSectionHeader(title = "ICONS & GLYPHS")
            NothingCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Icon Style", color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "monochrome" to "Monochrome",
                            "original" to "Original",
                            "monochrome_red" to "Red Accent"
                        ).forEach { (style, name) ->
                            val isSelected = config.iconStyle == style
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) DotlineRed else Color(0xFF1E1E1E))
                                    .clickable { viewModel.updateConfig(config.copy(iconStyle = style)) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(name, color = Color.White, fontFamily = SpaceMonoFontFamily, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    DotSeparator()
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Icon Shape", color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("circle" to "Circle", "squircle" to "Rounded Square").forEach { (shape, name) ->
                            val isSelected = config.iconShape == shape
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) DotlineRed else Color(0xFF1E1E1E))
                                    .clickable { viewModel.updateConfig(config.copy(iconShape = shape)) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(name, color = Color.White, fontFamily = SpaceMonoFontFamily, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    DotSeparator()
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show App Labels", color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp)
                        NothingSwitch(
                            checked = config.showLabels,
                            onCheckedChange = { viewModel.updateConfig(config.copy(showLabels = it)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    DotSeparator()
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Icon Size", color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp)
                        Text("${config.iconSizeDp} dp", color = DotlineTextSecondaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp)
                    }
                    Slider(
                        value = config.iconSizeDp.toFloat(),
                        onValueChange = { viewModel.updateConfig(config.copy(iconSizeDp = it.toInt())) },
                        valueRange = 44f..64f,
                        colors = SliderDefaults.colors(
                            thumbColor = DotlineRed,
                            activeTrackColor = DotlineRed,
                            inactiveTrackColor = Color(0xFF222222)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Clock & Gestures
            SettingsSectionHeader(title = "CLOCK & GESTURES")
            NothingCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("24-Hour Time Format", color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp)
                        NothingSwitch(
                            checked = config.timeFormat == "24",
                            onCheckedChange = { viewModel.updateConfig(config.copy(timeFormat = if (it) "24" else "12")) }
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    DotSeparator()
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Double Tap to Lock", color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp)
                            Text("Requires Accessibility Service permission", color = DotlineTextSecondaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 9.5.sp)
                        }
                        NothingSwitch(
                            checked = config.doubleTapLock,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    try {
                                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                                        Toast.makeText(context, "Enable Dotline in Accessibility Settings", Toast.LENGTH_LONG).show()
                                    } catch (_: Exception) {}
                                }
                                viewModel.updateConfig(config.copy(doubleTapLock = checked))
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    DotSeparator()
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Notification Dots", color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp)
                            Text("Displays minimalist red dots on app icons", color = DotlineTextSecondaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 9.5.sp)
                        }
                        NothingSwitch(
                            checked = config.notificationDots,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    try {
                                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                                        Toast.makeText(context, "Grant Notification Access to Dotline", Toast.LENGTH_LONG).show()
                                    } catch (_: Exception) {}
                                }
                                viewModel.updateConfig(config.copy(notificationDots = checked))
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Shortcuts
            SettingsSectionHeader(title = "STUDIOS & TOOLS")
            NothingCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsNavigationItem(
                        title = "Wallpaper Studio",
                        subtitle = "Procedural Nothing OS wallpapers",
                        onClick = { viewModel.navigateTo("wallpaper_studio") }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DotSeparator()
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingsNavigationItem(
                        title = "Sound Studio",
                        subtitle = "Minimal synthesizer tones & MediaStore export",
                        onClick = { viewModel.navigateTo("sound_studio") }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DotSeparator()
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingsNavigationItem(
                        title = "One UI Setup Guide",
                        subtitle = "Finish the look on Samsung Galaxy",
                        onClick = { viewModel.navigateTo("setup_guide") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Backup & Restore
            SettingsSectionHeader(title = "BACKUP & RESTORE")
            NothingCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NothingButton(
                            text = "EXPORT BACKUP",
                            onClick = {
                                val json = viewModel.exportBackup()
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("Dotline Backup", json))
                                Toast.makeText(context, "Backup copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            isPrimary = true
                        )
                        NothingButton(
                            text = "IMPORT BACKUP",
                            onClick = { showImportDialog = true },
                            modifier = Modifier.weight(1f),
                            isPrimary = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: About & Diagnostics
            SettingsSectionHeader(title = "ABOUT & DIAGNOSTICS")
            NothingCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Version", color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp)
                        Text("1.0 (Nothing OS 5.0)", color = DotlineTextSecondaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    DotSeparator()
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingsNavigationItem(
                        title = "Crash Log Viewer",
                        subtitle = "View uncaught exception logs for debugging",
                        onClick = { viewModel.navigateTo("crash_log") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showImportDialog) {
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                containerColor = Color(0xFF141414),
                title = { Text("Import Backup JSON", color = Color.White, fontFamily = DotoFontFamily) },
                text = {
                    TextField(
                        value = importText,
                        onValueChange = { importText = it },
                        placeholder = { Text("Paste JSON here", color = DotlineTextSecondaryDark) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0E0E0E),
                            unfocusedContainerColor = Color(0xFF0E0E0E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                },
                confirmButton = {
                    NothingButton(
                        text = "RESTORE",
                        onClick = {
                            val ok = viewModel.importBackup(importText)
                            showImportDialog = false
                            Toast.makeText(context, if (ok) "Backup restored!" else "Invalid backup JSON", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                dismissButton = {
                    NothingButton(text = "CANCEL", onClick = { showImportDialog = false }, isPrimary = false)
                }
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = DotlineTextSecondaryDark,
        fontFamily = SpaceMonoFontFamily,
        fontSize = 11.sp,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
private fun SettingsNavigationItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 13.sp)
            Text(subtitle, color = DotlineTextSecondaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 10.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = DotlineTextSecondaryDark, modifier = Modifier.size(18.dp))
    }
}
