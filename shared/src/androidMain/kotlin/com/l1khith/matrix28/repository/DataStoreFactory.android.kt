package com.l1khith.matrix28.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.l1khith.matrix28.data.AppContext
import okio.Path.Companion.toPath

actual fun createDataStore(): DataStore<Preferences> {
    val context = AppContext.get()
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { context.filesDir.resolve("user_preferences.preferences_pb").absolutePath.toPath() }
    )
}

fun createAndroidDataStore(context: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { context.filesDir.resolve("user_preferences.preferences_pb").absolutePath.toPath() }
    )
}
