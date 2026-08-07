package com.l1khith.matrix28.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.l1khith.matrix28.MainActivity
import com.l1khith.matrix28.R
import com.l1khith.matrix28.utils.FixedCalendarHelper
import com.l1khith.matrix28.utils.currentTimeMillis

class TodayTaskWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val today = FixedCalendarHelper.fromTimestamp(currentTimeMillis())
        val monthName = FixedCalendarHelper.getMonthName(today.month)

        for (widgetId in appWidgetIds) {
            try {
                val views = RemoteViews(context.packageName, R.layout.widget_today_tasks).apply {
                    setTextViewText(R.id.widget_date_text, "$monthName ${today.day}, ${today.year}")
                    setTextViewText(R.id.widget_cycle_badge, "Month ${today.month} • Day ${today.day} of 28")

                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    setOnClickPendingIntent(R.id.widget_root, pendingIntent)
                }
                appWidgetManager.updateAppWidget(widgetId, views)
            } catch (_: Exception) {}
        }
    }
}
