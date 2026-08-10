package com.l1khith.matrix28

import android.app.Application
import com.l1khith.matrix28.billing.SubscriptionManager
import com.l1khith.matrix28.data.AppContext

class MatrixApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)

        SubscriptionManager.configure(BuildConfig.REVENUECAT_API_KEY)

        // Register WidgetUpdater callback for Glance AppWidget real-time updates
        com.l1khith.matrix28.widget.WidgetUpdater.registerUpdateCallback {
            com.l1khith.matrix28.widget.TodayTaskWidget.updateWidget(this)
        }
    }
}



