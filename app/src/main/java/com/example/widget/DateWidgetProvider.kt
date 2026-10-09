package com.example.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val now = Date()
        val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())

        val dayStr = dayFormat.format(now)
        val monthStr = monthFormat.format(now).uppercase()

        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_date_layout)
            views.setTextViewText(R.id.widget_date_day, dayStr)
            views.setTextViewText(R.id.widget_date_month, monthStr)
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
