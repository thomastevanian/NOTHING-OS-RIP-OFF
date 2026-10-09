package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.ui.LauncherViewModel
import com.example.ui.components.AppItemView
import com.example.ui.components.NothingPillSearch
import com.example.ui.theme.DotlineCardDark
import com.example.ui.theme.DotlineOutlineDark
import com.example.ui.theme.DotlineRed
import com.example.ui.theme.DotlineTextPrimaryDark
import com.example.ui.theme.DotlineTextSecondaryDark
import com.example.ui.theme.DotoFontFamily
import com.example.ui.theme.SpaceMonoFontFamily
import kotlinx.coroutines.launch

@Composable
fun AppDrawerScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val config by viewModel.config.collectAsState()
    val allApps by viewModel.installedApps.collectAsState()
    val hiddenApps by viewModel.hiddenApps.collectAsState()
    val iconBitmaps by viewModel.iconBitmaps.collectAsState()
    val searchQuery by viewModel.drawerSearchQuery.collectAsState()
    val activeNotifs by viewModel.activeNotificationPackages.collectAsState()

    val filteredApps = remember(allApps, hiddenApps, searchQuery) {
        val visible = allApps.filter { !hiddenApps.contains(it.packageName) }
        if (searchQuery.isBlank()) {
            visible
        } else {
            visible.filter { it.label.contains(searchQuery, ignoreCase = true) }
        }
    }

    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    var selectedAppForMenu by remember { mutableStateOf<AppItem?>(null) }
    var activeScrollLetter by remember { mutableStateOf<Char?>(null) }

    val alphabet = ('A'..'Z').toList()

    BackHandler {
        if (searchQuery.isNotEmpty()) {
            viewModel.setDrawerSearchQuery("")
        } else {
            viewModel.navigateTo("home")
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Main App Grid + Search Bar
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 12.dp, top = 8.dp, bottom = 8.dp)
            ) {
                // Apps Grid
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(config.gridCols),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(filteredApps, key = { _, app -> app.id }) { _, app ->
                        AppItemView(
                            appItem = app,
                            iconBitmap = iconBitmaps[app.id],
                            iconSizeDp = config.iconSizeDp.dp,
                            showLabel = config.showLabels,
                            hasNotification = config.notificationDots && activeNotifs.contains(app.packageName),
                            onClick = { viewModel.launchApp(app) },
                            onLongClick = { selectedAppForMenu = app }
                        )
                    }
                }

                // Bottom Search Pill
                NothingPillSearch(
                    query = searchQuery,
                    onQueryChange = { viewModel.setDrawerSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }

            // Right edge A to Z index scroller
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(28.dp)
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                alphabet.forEach { letter ->
                    Text(
                        text = letter.toString(),
                        color = if (activeScrollLetter == letter) DotlineRed else DotlineTextSecondaryDark,
                        fontFamily = SpaceMonoFontFamily,
                        fontSize = 9.5.sp,
                        modifier = Modifier
                            .clickable {
                                activeScrollLetter = letter
                                val targetIndex = filteredApps.indexOfFirst {
                                    it.label.startsWith(letter, ignoreCase = true)
                                }
                                if (targetIndex >= 0) {
                                    coroutineScope.launch {
                                        gridState.animateScrollToItem(targetIndex)
                                    }
                                }
                            }
                            .padding(vertical = 1.dp)
                    )
                }
            }
        }

        // Floating letter bubble
        AnimatedVisibility(
            visible = activeScrollLetter != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF141414))
                    .border(1.dp, DotlineRed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeScrollLetter?.toString() ?: "",
                    color = Color.White,
                    fontFamily = DotoFontFamily,
                    fontSize = 32.sp
                )
            }
        }

        // App Options Popup
        selectedAppForMenu?.let { app ->
            DropdownMenu(
                expanded = true,
                onDismissRequest = { selectedAppForMenu = null },
                modifier = Modifier
                    .background(DotlineCardDark)
                    .border(1.dp, DotlineOutlineDark, RoundedCornerShape(12.dp))
            ) {
                DropdownMenuItem(
                    text = { Text("Add to Home", color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Add, null, tint = DotlineTextSecondaryDark, modifier = Modifier.size(16.dp)) },
                    onClick = {
                        viewModel.addAppToHome(app)
                        selectedAppForMenu = null
                    }
                )
                DropdownMenuItem(
                    text = { Text("App Info", color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Info, null, tint = DotlineTextSecondaryDark, modifier = Modifier.size(16.dp)) },
                    onClick = {
                        viewModel.openAppInfo(app)
                        selectedAppForMenu = null
                    }
                )
                DropdownMenuItem(
                    text = { Text("Hide App", color = DotlineTextSecondaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.VisibilityOff, null, tint = DotlineTextSecondaryDark, modifier = Modifier.size(16.dp)) },
                    onClick = {
                        viewModel.toggleHideApp(app.packageName)
                        selectedAppForMenu = null
                    }
                )
                DropdownMenuItem(
                    text = { Text("Uninstall", color = DotlineRed, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Delete, null, tint = DotlineRed, modifier = Modifier.size(16.dp)) },
                    onClick = {
                        viewModel.uninstallApp(app)
                        selectedAppForMenu = null
                    }
                )
            }
        }
    }
}
