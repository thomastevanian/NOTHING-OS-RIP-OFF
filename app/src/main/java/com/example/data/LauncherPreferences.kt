package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.LauncherConfig
import org.json.JSONArray
import org.json.JSONObject

class LauncherPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("dotline_launcher_prefs", Context.MODE_PRIVATE)

    fun loadConfig(): LauncherConfig {
        return LauncherConfig(
            gridCols = prefs.getInt("grid_cols", 5),
            gridRows = prefs.getInt("grid_rows", 6),
            iconStyle = prefs.getString("icon_style", "monochrome") ?: "monochrome",
            iconShape = prefs.getString("icon_shape", "circle") ?: "circle",
            iconSizeDp = prefs.getInt("icon_size_dp", 52),
            showLabels = prefs.getBoolean("show_labels", true),
            themeMode = prefs.getString("theme_mode", "dark") ?: "dark",
            clockStyle = prefs.getString("clock_style", "dotmatrix") ?: "dotmatrix",
            timeFormat = prefs.getString("time_format", "24") ?: "24",
            hideClock = prefs.getBoolean("hide_clock", false),
            notificationDots = prefs.getBoolean("notification_dots", false),
            doubleTapLock = prefs.getBoolean("double_tap_lock", false),
            autoOpenKeyboard = prefs.getBoolean("auto_open_keyboard", false),
            infiniteScroll = prefs.getBoolean("infinite_scroll", false),
            quickNotesContent = prefs.getString("quick_notes", "Nothing to remember.") ?: "Nothing to remember.",
            weatherCity = prefs.getString("weather_city", "London") ?: "London",
            weatherTemp = prefs.getString("weather_temp", "20°C") ?: "20°C",
            weatherCondition = prefs.getString("weather_cond", "CLEAR SKY") ?: "CLEAR SKY"
        )
    }

    fun saveConfig(config: LauncherConfig) {
        prefs.edit()
            .putInt("grid_cols", config.gridCols)
            .putInt("grid_rows", config.gridRows)
            .putString("icon_style", config.iconStyle)
            .putString("icon_shape", config.iconShape)
            .putInt("icon_size_dp", config.iconSizeDp)
            .putBoolean("show_labels", config.showLabels)
            .putString("theme_mode", config.themeMode)
            .putString("clock_style", config.clockStyle)
            .putString("time_format", config.timeFormat)
            .putBoolean("hide_clock", config.hideClock)
            .putBoolean("notification_dots", config.notificationDots)
            .putBoolean("double_tap_lock", config.doubleTapLock)
            .putBoolean("auto_open_keyboard", config.autoOpenKeyboard)
            .putBoolean("infinite_scroll", config.infiniteScroll)
            .putString("quick_notes", config.quickNotesContent)
            .putString("weather_city", config.weatherCity)
            .putString("weather_temp", config.weatherTemp)
            .putString("weather_cond", config.weatherCondition)
            .apply()
    }

    fun getHiddenApps(): Set<String> {
        return prefs.getStringSet("hidden_apps", emptySet()) ?: emptySet()
    }

    fun setHiddenApps(hidden: Set<String>) {
        prefs.edit().putStringSet("hidden_apps", hidden).apply()
    }

    fun getDockAppIds(): List<String> {
        val jsonStr = prefs.getString("dock_apps_json", null) ?: return emptyList()
        val list = mutableListOf<String>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                list.add(arr.getString(i))
            }
        } catch (_: Exception) {}
        return list
    }

    fun setDockAppIds(ids: List<String>) {
        val arr = JSONArray()
        ids.forEach { arr.put(it) }
        prefs.edit().putString("dock_apps_json", arr.toString()).apply()
    }

    fun isOnboardingDone(): Boolean {
        return prefs.getBoolean("onboarding_done", false)
    }

    fun setOnboardingDone(done: Boolean) {
        prefs.edit().putBoolean("onboarding_done", done).apply()
    }

    // Export entire setup to JSON string
    fun exportBackupJson(): String {
        val root = JSONObject()
        val allEntries = prefs.all
        for ((k, v) in allEntries) {
            when (v) {
                is String -> root.put(k, v)
                is Boolean -> root.put(k, v)
                is Int -> root.put(k, v)
                is Long -> root.put(k, v)
                is Float -> root.put(k, v)
                is Set<*> -> {
                    val arr = JSONArray()
                    v.forEach { arr.put(it) }
                    root.put(k, arr)
                }
            }
        }
        return root.toString(2)
    }

    // Import from JSON string
    fun importBackupJson(jsonStr: String): Boolean {
        return try {
            val root = JSONObject(jsonStr)
            val editor = prefs.edit()
            val keys = root.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                when (val value = root.get(key)) {
                    is Boolean -> editor.putBoolean(key, value)
                    is Int -> editor.putInt(key, value)
                    is Long -> editor.putLong(key, value)
                    is Double -> editor.putFloat(key, value.toFloat())
                    is String -> editor.putString(key, value)
                    is JSONArray -> {
                        val set = mutableSetOf<String>()
                        for (i in 0 until value.length()) {
                            set.add(value.getString(i))
                        }
                        editor.putStringSet(key, set)
                    }
                }
            }
            editor.apply()
            true
        } catch (_: Exception) {
            false
        }
    }
}
