package com.l1khith.matrix28

import android.app.Application
import com.l1khith.matrix28.data.AppContext

class MatrixApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
    }
}
