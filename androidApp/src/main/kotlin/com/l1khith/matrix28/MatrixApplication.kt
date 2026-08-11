package com.l1khith.matrix28

import android.app.Application
import com.l1khith.matrix28.billing.SubscriptionManager
import com.l1khith.matrix28.data.AppContext
import com.l1khith.matrix28.utils.CalendarContentObserver
import com.l1khith.matrix28.widget.TodayTaskWidget
import com.l1khith.matrix28.widget.WidgetUpdater

class MatrixApplication : Application() {

    private val widgetCallback = {
        TodayTaskWidget.updateWidget(this)
    }

    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)

        SubscriptionManager.configure(BuildConfig.REVENUECAT_API_KEY)

        // Register Notification Channel for task reminders
        com.l1khith.matrix28.receiver.AlarmReceiver.createNotificationChannel(this)

        // Register WidgetUpdater callback safely using single instance
        WidgetUpdater.registerUpdateCallback(widgetCallback)

        // Register real-time automatic Google Calendar observer
        CalendarContentObserver.register()
    }
}
