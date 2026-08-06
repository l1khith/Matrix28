package com.l1khith.matrix28.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
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
            val views = RemoteViews(context.packageName, android.R.layout.simple_list_item_2).apply {
                setTextViewText(android.R.id.text1, "$monthName ${today.day}, ${today.year}")
                setTextViewText(android.R.id.text2, "Matrix 28 • Today's Tasks")
            }
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}
