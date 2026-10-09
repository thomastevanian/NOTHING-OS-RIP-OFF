package com.example.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val now = Date()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        val timeStr = timeFormat.format(now)
        val dateStr = dateFormat.format(now).uppercase()

        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_clock_layout)
            views.setTextViewText(R.id.widget_clock_time, timeStr)
            views.setTextViewText(R.id.widget_clock_date, dateStr)
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
