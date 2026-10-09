package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.BatteryMonitor
import com.example.data.CalendarEvent
import com.example.data.CalendarRepository
import com.example.data.IconManager
import com.example.data.LauncherPreferences
import com.example.data.WeatherData
import com.example.data.WeatherRepository
import com.example.model.AppItem
import com.example.model.FolderItem
import com.example.model.LauncherConfig
import com.example.model.LauncherGridItem
import com.example.model.WidgetType
import com.example.service.DotlineAccessibilityService
import com.example.service.DotlineNotificationListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = LauncherPreferences(application)
    private val appRepo = AppRepository(application)
    val iconManager = IconManager(application)
    private val batteryMonitor = BatteryMonitor(application)
    private val weatherRepo = WeatherRepository(application)
    private val calendarRepo = CalendarRepository(application)

    // Config & Preferences
    private val _config = MutableStateFlow(prefs.loadConfig())
    val config: StateFlow<LauncherConfig> = _config.asStateFlow()

    // Apps & Drawer
    val installedApps: StateFlow<List<AppItem>> = appRepo.apps
    private val _drawerSearchQuery = MutableStateFlow("")
    val drawerSearchQuery: StateFlow<String> = _drawerSearchQuery.asStateFlow()

    // Layout
    private val _gridItems = MutableStateFlow<List<LauncherGridItem>>(emptyList())
    val gridItems: StateFlow<List<LauncherGridItem>> = _gridItems.asStateFlow()

    private val _dockApps = MutableStateFlow<List<AppItem>>(emptyList())
    val dockApps: StateFlow<List<AppItem>> = _dockApps.asStateFlow()

    private val _hiddenApps = MutableStateFlow(prefs.getHiddenApps())
    val hiddenApps: StateFlow<Set<String>> = _hiddenApps.asStateFlow()

    // Navigation & UI States
    private val _currentScreen = MutableStateFlow(if (prefs.isOnboardingDone()) "home" else "onboarding")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _activeFolder = MutableStateFlow<LauncherGridItem.Folder?>(null)
    val activeFolder: StateFlow<LauncherGridItem.Folder?> = _activeFolder.asStateFlow()

    private val _editingNote = MutableStateFlow(false)
    val editingNote: StateFlow<Boolean> = _editingNote.asStateFlow()

    // Telemetry
    val batteryLevel: StateFlow<Int> = batteryMonitor.batteryLevel
    val isCharging: StateFlow<Boolean> = batteryMonitor.isCharging

    private val _weatherData = MutableStateFlow(WeatherData())
    val weatherData: StateFlow<WeatherData> = _weatherData.asStateFlow()

    private val _nextCalendarEvent = MutableStateFlow<CalendarEvent?>(null)
    val nextCalendarEvent: StateFlow<CalendarEvent?> = _nextCalendarEvent.asStateFlow()

    private val _currentTime = MutableStateFlow("")
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()

    private val _currentDate = MutableStateFlow("")
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    // Icon Cache
    private val _iconBitmaps = MutableStateFlow<Map<String, Bitmap>>(emptyMap())
    val iconBitmaps: StateFlow<Map<String, Bitmap>> = _iconBitmaps.asStateFlow()

    // Active notifications
    val activeNotificationPackages: StateFlow<Set<String>> = DotlineNotificationListener.activePackages

    init {
        batteryMonitor.start()
        updateClock()
        startClockTicker()

        viewModelScope.launch {
            installedApps.collect { apps ->
                if (apps.isNotEmpty()) {
                    initLayoutIfEmpty(apps)
                    preloadIcons(apps)
                }
            }
        }

        refreshWeather()
        refreshCalendar()
    }

    private fun startClockTicker() {
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                updateClock()
                // Align to next minute
                val now = System.currentTimeMillis()
                val sleepMs = 60000 - (now % 60000)
                delay(maxOf(500, sleepMs))
            }
        }
    }

    fun updateClock() {
        val now = Date()
        val tf = if (_config.value.timeFormat == "12") "h:mm" else "HH:mm"
        _currentTime.value = SimpleDateFormat(tf, Locale.getDefault()).format(now)
        _currentDate.value = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(now).uppercase()
    }

    private fun initLayoutIfEmpty(apps: List<AppItem>) {
        if (_gridItems.value.isEmpty()) {
            val list = mutableListOf<LauncherGridItem>()
            // Top Clock Widget on Page 0
            list.add(
                LauncherGridItem.BuiltinWidget(
                    id = "widget_clock_0",
                    page = 0,
                    col = 0,
                    row = 0,
                    type = WidgetType.CLOCK_DOTMATRIX
                )
            )

            // Top Weather Widget
            list.add(
                LauncherGridItem.BuiltinWidget(
                    id = "widget_weather_0",
                    page = 0,
                    col = 0,
                    row = 2,
                    type = WidgetType.WEATHER
                )
            )

            // Battery & Date Widget side-by-side
            list.add(
                LauncherGridItem.BuiltinWidget(
                    id = "widget_battery_0",
                    page = 0,
                    col = 0,
                    row = 4,
                    type = WidgetType.BATTERY
                )
            )
            list.add(
                LauncherGridItem.BuiltinWidget(
                    id = "widget_date_0",
                    page = 0,
                    col = 2,
                    row = 4,
                    type = WidgetType.DATE
                )
            )

            // Fill page 1 with apps
            val unhidden = apps.filter { !_hiddenApps.value.contains(it.packageName) }
            unhidden.take(16).forEachIndexed { index, app ->
                val col = index % 4
                val row = index / 4
                list.add(
                    LauncherGridItem.App(
                        id = "app_p1_${app.id}",
                        page = 1,
                        col = col,
                        row = row,
                        appItem = app
                    )
                )
            }

            _gridItems.value = list

            // Default dock apps: Phone, Messages, Browser, Camera
            val dockIds = prefs.getDockAppIds()
            if (dockIds.isNotEmpty()) {
                val matched = apps.filter { dockIds.contains(it.packageName) }
                _dockApps.value = matched
            } else {
                val dockDefaults = apps.filter {
                    val p = it.packageName.lowercase()
                    p.contains("dialer") || p.contains("phone") ||
                    p.contains("message") || p.contains("messaging") ||
                    p.contains("chrome") || p.contains("browser") ||
                    p.contains("camera")
                }.distinctBy { it.packageName }.take(4)
                _dockApps.value = if (dockDefaults.isNotEmpty()) dockDefaults else apps.take(4)
            }
        }
    }

    private fun preloadIcons(apps: List<AppItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            val map = _iconBitmaps.value.toMutableMap()
            val cfg = _config.value
            for (app in apps.take(80)) {
                if (!map.containsKey(app.id)) {
                    val bmp = iconManager.getIcon(app, cfg.iconStyle, cfg.iconShape, cfg.iconSizeDp * 3)
                    map[app.id] = bmp
                }
            }
            _iconBitmaps.value = map
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            _weatherData.value = weatherRepo.getWeather(city = _config.value.weatherCity)
        }
    }

    fun refreshCalendar() {
        viewModelScope.launch {
            _nextCalendarEvent.value = calendarRepo.getNextEvent()
        }
    }

    fun launchApp(app: AppItem) {
        appRepo.launchApp(app)
        closeAllOverlays()
    }

    fun openAppInfo(app: AppItem) {
        appRepo.openAppInfo(app)
    }

    fun uninstallApp(app: AppItem) {
        appRepo.uninstallApp(app)
    }

    fun setDrawerSearchQuery(query: String) {
        _drawerSearchQuery.value = query
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
        _isEditMode.value = false
    }

    fun setEditMode(enabled: Boolean) {
        _isEditMode.value = enabled
    }

    fun openFolder(folder: LauncherGridItem.Folder) {
        _activeFolder.value = folder
    }

    fun closeFolder() {
        _activeFolder.value = null
    }

    fun updateFolderTitle(folderId: String, newTitle: String) {
        _gridItems.value = _gridItems.value.map { item ->
            if (item is LauncherGridItem.Folder && item.id == folderId) {
                item.copy(title = newTitle)
            } else item
        }
    }

    fun removeGridItem(id: String) {
        _gridItems.value = _gridItems.value.filter { it.id != id }
    }

    fun addWidget(type: WidgetType, page: Int = 0) {
        val newItem = LauncherGridItem.BuiltinWidget(
            id = "widget_${type.name}_${System.currentTimeMillis()}",
            page = page,
            col = 0,
            row = 0,
            type = type
        )
        _gridItems.value = _gridItems.value + newItem
        _isEditMode.value = false
    }

    fun addAppToHome(app: AppItem, page: Int = 1) {
        val newItem = LauncherGridItem.App(
            id = "app_${app.id}_${System.currentTimeMillis()}",
            page = page,
            col = 0,
            row = 0,
            appItem = app
        )
        _gridItems.value = _gridItems.value + newItem
    }

    fun toggleHideApp(packageName: String) {
        val current = _hiddenApps.value.toMutableSet()
        if (current.contains(packageName)) current.remove(packageName) else current.add(packageName)
        _hiddenApps.value = current
        prefs.setHiddenApps(current)
    }

    fun updateConfig(newConfig: LauncherConfig) {
        _config.value = newConfig
        prefs.saveConfig(newConfig)
        updateClock()
        // Re-generate icons with new style/shape
        viewModelScope.launch(Dispatchers.IO) {
            val map = mutableMapOf<String, Bitmap>()
            for (app in installedApps.value.take(80)) {
                map[app.id] = iconManager.getIcon(app, newConfig.iconStyle, newConfig.iconShape, newConfig.iconSizeDp * 3)
            }
            _iconBitmaps.value = map
        }
    }

    fun finishOnboarding() {
        prefs.setOnboardingDone(true)
        _currentScreen.value = "home"
    }

    fun lockDevice(): Boolean {
        return DotlineAccessibilityService.lockDevice()
    }

    fun setEditingNote(editing: Boolean) {
        _editingNote.value = editing
    }

    fun saveNoteContent(note: String) {
        updateConfig(_config.value.copy(quickNotesContent = note))
        _editingNote.value = false
    }

    fun exportBackup(): String = prefs.exportBackupJson()

    fun importBackup(json: String): Boolean {
        val ok = prefs.importBackupJson(json)
        if (ok) {
            _config.value = prefs.loadConfig()
            _hiddenApps.value = prefs.getHiddenApps()
        }
        return ok
    }

    fun closeAllOverlays() {
        _isEditMode.value = false
        _activeFolder.value = null
        _editingNote.value = false
        _drawerSearchQuery.value = ""
        if (_currentScreen.value != "home" && _currentScreen.value != "onboarding") {
            _currentScreen.value = "home"
        }
    }

    override fun onCleared() {
        super.onCleared()
        batteryMonitor.stop()
        appRepo.cleanup()
        iconManager.onTrimMemory()
    }
}
