package com.example.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.R
import com.example.data.LauncherPreferences

class WeatherWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val prefs = LauncherPreferences(context)
        val config = prefs.loadConfig()

        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_weather_layout)
            views.setTextViewText(R.id.widget_weather_temp, config.weatherTemp)
            views.setTextViewText(R.id.widget_weather_desc, config.weatherCondition)
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
