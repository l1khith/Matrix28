package com.l1khith.matrix28

import android.app.Application
import com.l1khith.matrix28.billing.SubscriptionManager
import com.l1khith.matrix28.data.AppContext

class MatrixApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)

        // Initialize RevenueCat SDK with API key from BuildConfig (loaded from secrets.properties)
        SubscriptionManager.configure(BuildConfig.REVENUECAT_API_KEY)
    }
}

