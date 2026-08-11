package com.l1khith.matrix28

import android.app.Application
import com.l1khith.matrix28.billing.SubscriptionManager
import com.l1khith.matrix28.data.AppContext
import com.l1khith.matrix28.utils.CalendarContentObserver

class MatrixApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)

        SubscriptionManager.configure(BuildConfig.REVENUECAT_API_KEY)

        com.l1khith.matrix28.receiver.AlarmReceiver.createNotificationChannel(this)

        CalendarContentObserver.register()
    }
}
