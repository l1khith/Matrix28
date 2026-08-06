package com.l1khith.matrix28.utils

actual object ExportUtils {
    actual fun shareMonthViewImage(
        monthName: String,
        year: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // Android Canvas Bitmap export trigger
            onSuccess()
        } catch (e: Exception) {
            onError(e.message ?: "Failed to export month view image")
        }
    }
}
