package com.l1khith.matrix28.utils

expect object ExportUtils {
    fun shareMonthViewImage(
        monthName: String,
        year: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}
