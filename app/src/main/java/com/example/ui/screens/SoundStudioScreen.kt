package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sound.SoundCategory
import com.example.sound.SoundPreset
import com.example.sound.SoundSynthesizer
import com.example.ui.LauncherViewModel
import com.example.ui.components.DotProgressBar
import com.example.ui.components.DotSeparator
import com.example.ui.theme.DotlineCardDark
import com.example.ui.theme.DotlineOutlineDark
import com.example.ui.theme.DotlineRed
import com.example.ui.theme.DotlineTextPrimaryDark
import com.example.ui.theme.DotlineTextSecondaryDark
import com.example.ui.theme.DotoFontFamily
import com.example.ui.theme.SpaceMonoFontFamily
import kotlinx.coroutines.launch

@Composable
fun SoundStudioScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedCategory by remember { mutableStateOf(SoundCategory.NOTIFICATION) }
    var playingPresetId by remember { mutableStateOf<String?>(null) }

    val presets = when (selectedCategory) {
        SoundCategory.RINGTONE -> SoundSynthesizer.ringtones
        SoundCategory.NOTIFICATION -> SoundSynthesizer.notifications
        SoundCategory.ALARM -> SoundSynthesizer.alarms
        SoundCategory.UI_CLICK -> SoundSynthesizer.uiClicks
    }

    BackHandler {
        SoundSynthesizer.stopPreview()
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
                        .clickable {
                            SoundSynthesizer.stopPreview()
                            viewModel.navigateTo("home")
                        },
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
                    text = "SOUND STUDIO",
                    color = DotlineTextPrimaryDark,
                    fontFamily = DotoFontFamily,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selector Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SoundCategory.values()) { cat ->
                    val isSelected = selectedCategory == cat
                    val title = when (cat) {
                        SoundCategory.RINGTONE -> "RINGTONES"
                        SoundCategory.NOTIFICATION -> "NOTIFICATIONS"
                        SoundCategory.ALARM -> "ALARMS"
                        SoundCategory.UI_CLICK -> "UI CLICKS"
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) DotlineRed else Color(0xFF141414))
                            .border(1.dp, if (isSelected) DotlineRed else DotlineOutlineDark, RoundedCornerShape(14.dp))
                            .clickable {
                                SoundSynthesizer.stopPreview()
                                playingPresetId = null
                                selectedCategory = cat
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else DotlineTextSecondaryDark,
                            fontFamily = SpaceMonoFontFamily,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            DotSeparator()
            Spacer(modifier = Modifier.height(16.dp))

            // Sound List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(presets) { preset ->
                    SoundItemCard(
                        preset = preset,
                        isPlaying = playingPresetId == preset.id,
                        onPlay = {
                            if (playingPresetId == preset.id) {
                                SoundSynthesizer.stopPreview()
                                playingPresetId = null
                            } else {
                                SoundSynthesizer.playPreview(preset)
                                playingPresetId = preset.id
                            }
                        },
                        onSave = {
                            coroutineScope.launch {
                                val uri = SoundSynthesizer.saveToMediaStore(context, preset)
                                Toast.makeText(
                                    context,
                                    if (uri != null) "Saved ${preset.name} to device" else "Failed to save audio",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SoundItemCard(
    preset: SoundPreset,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onSave: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DotlineCardDark)
            .border(1.dp, if (isPlaying) DotlineRed else DotlineOutlineDark, RoundedCornerShape(18.dp))
            .clickable(onClick = onPlay)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) DotlineRed else Color(0xFF1E1E1E)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = "Play/Stop",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = preset.name,
                        color = DotlineTextPrimaryDark,
                        fontFamily = SpaceMonoFontFamily,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${preset.durationMs}ms · PCM Synthesizer",
                        color = DotlineTextSecondaryDark,
                        fontFamily = SpaceMonoFontFamily,
                        fontSize = 10.sp
                    )
                }
            }

            if (preset.category != SoundCategory.UI_CLICK) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF181818))
                        .clickable(onClick = onSave),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Save Sound",
                        tint = DotlineTextSecondaryDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
