package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.LauncherGridItem
import com.example.model.WidgetType
import com.example.ui.LauncherViewModel
import com.example.ui.components.AppItemView
import com.example.ui.components.BuiltinBatteryWidget
import com.example.ui.components.BuiltinCalendarWidget
import com.example.ui.components.BuiltinClockWidget
import com.example.ui.components.BuiltinDateWidget
import com.example.ui.components.BuiltinQuickNotesWidget
import com.example.ui.components.BuiltinWeatherWidget
import com.example.ui.components.BuiltinWorldClockWidget
import com.example.ui.components.DotPageIndicator
import com.example.ui.components.DotSeparator
import com.example.ui.components.DottedGridOverlay
import com.example.ui.components.FolderDialog
import com.example.ui.components.FolderItemView
import com.example.ui.components.NothingButton
import com.example.ui.components.NothingCard
import com.example.ui.theme.DotlineCardDark
import com.example.ui.theme.DotlineOutlineDark
import com.example.ui.theme.DotlineRed
import com.example.ui.theme.DotlineTextPrimaryDark
import com.example.ui.theme.DotlineTextSecondaryDark
import com.example.ui.theme.DotoFontFamily
import com.example.ui.theme.SpaceMonoFontFamily
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val config by viewModel.config.collectAsState()
    val gridItems by viewModel.gridItems.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()
    val iconBitmaps by viewModel.iconBitmaps.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val activeFolder by viewModel.activeFolder.collectAsState()
    val timeString by viewModel.currentTime.collectAsState()
    val dateString by viewModel.currentDate.collectAsState()
    val batteryLevel by viewModel.batteryLevel.collectAsState()
    val isCharging by viewModel.isCharging.collectAsState()
    val weatherData by viewModel.weatherData.collectAsState()
    val nextEvent by viewModel.nextCalendarEvent.collectAsState()
    val activeNotifs by viewModel.activeNotificationPackages.collectAsState()

    val pageCount = 3
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pageCount })
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedAppForMenu by remember { mutableStateOf<AppItem?>(null) }
    var showWidgetPicker by remember { mutableStateOf(false) }

    BackHandler {
        if (isEditMode) {
            viewModel.setEditMode(false)
        } else if (pagerState.currentPage != 0) {
            coroutineScope.launch { pagerState.animateScrollToPage(0) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (config.doubleTapLock) {
                            viewModel.lockDevice()
                        }
                    },
                    onLongPress = {
                        viewModel.setEditMode(true)
                    }
                )
            }
            .pointerInput(Unit) {
                var totalDragY = 0f
                detectVerticalDragGestures(
                    onDragStart = { totalDragY = 0f },
                    onDragEnd = {
                        if (totalDragY < -120f) {
                            // Swipe up opens app drawer
                            viewModel.navigateTo("drawer")
                        } else if (totalDragY > 150f) {
                            // Swipe down expands notifications shade
                            expandNotificationShade(context)
                        }
                    },
                    onVerticalDrag = { _, dragAmount ->
                        totalDragY += dragAmount
                    }
                )
            }
    ) {
        // Dotted grid overlay in edit mode
        if (isEditMode) {
            DottedGridOverlay(modifier = Modifier.fillMaxSize())
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Main Multi-page Horizontal Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                PageContent(
                    page = pageIndex,
                    gridItems = gridItems.filter { it.page == pageIndex },
                    iconBitmaps = iconBitmaps,
                    config = config,
                    timeString = timeString,
                    dateString = dateString,
                    batteryLevel = batteryLevel,
                    isCharging = isCharging,
                    weatherData = weatherData,
                    nextEvent = nextEvent,
                    activeNotifs = activeNotifs,
                    isEditMode = isEditMode,
                    onAppClick = { viewModel.launchApp(it) },
                    onAppLongClick = { selectedAppForMenu = it },
                    onFolderClick = { viewModel.openFolder(it) },
                    onRemoveItem = { viewModel.removeGridItem(it) }
                )
            }

            // Dot Page Indicator
            DotPageIndicator(
                pageCount = pageCount,
                currentPage = pagerState.currentPage,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                activeColor = DotlineRed
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dock (4 Apps)
            DockRow(
                dockApps = dockApps,
                iconBitmaps = iconBitmaps,
                config = config,
                activeNotifs = activeNotifs,
                onAppClick = { viewModel.launchApp(it) },
                onAppLongClick = { selectedAppForMenu = it }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Edit Mode Bottom Toolbar
        AnimatedVisibility(
            visible = isEditMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            EditModeBar(
                onAddWidget = { showWidgetPicker = true },
                onWallpaper = { viewModel.navigateTo("wallpaper_studio") },
                onSettings = { viewModel.navigateTo("settings") },
                onDone = { viewModel.setEditMode(false) }
            )
        }

        // Widget Picker Bottom Sheet / Dialog
        if (showWidgetPicker) {
            WidgetPickerDialog(
                onSelectWidget = { type ->
                    viewModel.addWidget(type, pagerState.currentPage)
                    showWidgetPicker = false
                },
                onDismiss = { showWidgetPicker = false }
            )
        }

        // App Context Menu (App info, Uninstall, Remove)
        selectedAppForMenu?.let { app ->
            DropdownMenu(
                expanded = true,
                onDismissRequest = { selectedAppForMenu = null },
                modifier = Modifier
                    .background(DotlineCardDark)
                    .border(1.dp, DotlineOutlineDark, RoundedCornerShape(12.dp))
            ) {
                DropdownMenuItem(
                    text = { Text("App Info", color = DotlineTextPrimaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Info, null, tint = DotlineTextSecondaryDark, modifier = Modifier.size(16.dp)) },
                    onClick = {
                        viewModel.openAppInfo(app)
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

        // Active Folder Dialog
        activeFolder?.let { folder ->
            FolderDialog(
                title = folder.title,
                apps = folder.apps,
                iconBitmaps = iconBitmaps,
                onTitleChange = { viewModel.updateFolderTitle(folder.id, it) },
                onAppClick = { viewModel.launchApp(it) },
                onDismiss = { viewModel.closeFolder() }
            )
        }
    }
}

@Composable
private fun PageContent(
    page: Int,
    gridItems: List<LauncherGridItem>,
    iconBitmaps: Map<String, Bitmap>,
    config: com.example.model.LauncherConfig,
    timeString: String,
    dateString: String,
    batteryLevel: Int,
    isCharging: Boolean,
    weatherData: com.example.data.WeatherData,
    nextEvent: com.example.data.CalendarEvent?,
    activeNotifs: Set<String>,
    isEditMode: Boolean,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    onFolderClick: (LauncherGridItem.Folder) -> Unit,
    onRemoveItem: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Builtin Widgets & Items
        gridItems.forEach { item ->
            Box(modifier = Modifier.padding(vertical = 6.dp)) {
                when (item) {
                    is LauncherGridItem.BuiltinWidget -> {
                        when (item.type) {
                            WidgetType.CLOCK_DOTMATRIX -> {
                                BuiltinClockWidget(
                                    timeString = timeString,
                                    dateString = dateString,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                )
                            }
                            WidgetType.BATTERY -> {
                                BuiltinBatteryWidget(
                                    batteryLevel = batteryLevel,
                                    isCharging = isCharging,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                )
                            }
                            WidgetType.WEATHER -> {
                                BuiltinWeatherWidget(
                                    weather = weatherData,
                                    city = config.weatherCity,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                )
                            }
                            WidgetType.DATE -> {
                                val now = Date()
                                val day = SimpleDateFormat("dd", Locale.getDefault()).format(now)
                                val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(now)
                                val weekday = SimpleDateFormat("EEEE", Locale.getDefault()).format(now)
                                BuiltinDateWidget(
                                    dayString = day,
                                    monthString = month,
                                    weekdayString = weekday,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                )
                            }
                            WidgetType.CALENDAR -> {
                                BuiltinCalendarWidget(
                                    nextEvent = nextEvent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                )
                            }
                            WidgetType.WORLD_CLOCK -> {
                                BuiltinWorldClockWidget(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(95.dp)
                                )
                            }
                            WidgetType.QUICK_NOTES -> {
                                BuiltinQuickNotesWidget(
                                    noteContent = config.quickNotesContent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                )
                            }
                        }
                    }
                    is LauncherGridItem.App -> {
                        AppItemView(
                            appItem = item.appItem,
                            iconBitmap = iconBitmaps[item.appItem.id],
                            iconSizeDp = config.iconSizeDp.dp,
                            showLabel = config.showLabels,
                            hasNotification = config.notificationDots && activeNotifs.contains(item.appItem.packageName),
                            onClick = { onAppClick(item.appItem) },
                            onLongClick = { onAppLongClick(item.appItem) }
                        )
                    }
                    is LauncherGridItem.Folder -> {
                        FolderItemView(
                            title = item.title,
                            apps = item.apps,
                            iconBitmaps = iconBitmaps,
                            sizeDp = config.iconSizeDp.dp,
                            showLabel = config.showLabels,
                            onClick = { onFolderClick(item) },
                            onLongClick = {}
                        )
                    }
                }

                if (isEditMode) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(DotlineRed)
                            .clickable { onRemoveItem(item.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DockRow(
    dockApps: List<AppItem>,
    iconBitmaps: Map<String, Bitmap>,
    config: com.example.model.LauncherConfig,
    activeNotifs: Set<String>,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        dockApps.take(4).forEach { app ->
            AppItemView(
                appItem = app,
                iconBitmap = iconBitmaps[app.id],
                iconSizeDp = config.iconSizeDp.dp,
                showLabel = false, // Dock icons have no label in Nothing OS
                hasNotification = config.notificationDots && activeNotifs.contains(app.packageName),
                onClick = { onAppClick(app) },
                onLongClick = { onAppLongClick(app) }
            )
        }
    }
}

@Composable
private fun EditModeBar(
    onAddWidget: () -> Unit,
    onWallpaper: () -> Unit,
    onSettings: () -> Unit,
    onDone: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF121212))
            .border(1.dp, DotlineOutlineDark, RoundedCornerShape(32.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(
                imageVector = Icons.Default.Widgets,
                contentDescription = "Widgets",
                tint = Color.White,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onAddWidget)
            )
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "Wallpaper",
                tint = Color.White,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onWallpaper)
            )
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onSettings)
            )
        }
        NothingButton(
            text = "DONE",
            onClick = onDone,
            isPrimary = true
        )
    }
}

@Composable
private fun WidgetPickerDialog(
    onSelectWidget: (WidgetType) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        NothingCard(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "WIDGETS",
                    color = DotlineTextPrimaryDark,
                    fontFamily = DotoFontFamily,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                DotSeparator()
                Spacer(modifier = Modifier.height(16.dp))

                WidgetType.values().forEach { widget ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectWidget(widget) }
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = widget.title,
                            color = DotlineTextPrimaryDark,
                            fontFamily = SpaceMonoFontFamily,
                            fontSize = 13.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = DotlineRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun expandNotificationShade(context: Context) {
    try {
        val sbservice = context.getSystemService("statusbar")
        val statusbarManager = Class.forName("android.app.StatusBarManager")
        val expands = statusbarManager.getMethod("expandNotificationsPanel")
        expands.invoke(sbservice)
    } catch (_: Exception) {}
}
