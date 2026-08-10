package com.l1khith.matrix28.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import okio.Path.Companion.toPath

actual fun createDataStore(): DataStore<Preferences> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    val path = requireNotNull(documentDirectory?.path) + "/user_preferences.preferences_pb"
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { path.toPath() }
    )
}
