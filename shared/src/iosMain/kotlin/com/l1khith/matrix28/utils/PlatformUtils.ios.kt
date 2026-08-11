package com.l1khith.matrix28.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSError
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthentication
import platform.UIKit.UIPasteboard
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

actual fun copyToClipboard(text: String) {
    UIPasteboard.generalPasteboard.string = text
}

actual fun showPlatformToast(message: String) {
    // iOS no-op
}

@Composable
actual fun PlatformTimePicker(
    show: Boolean,
    initialTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    if (show) {
        TimePickerDialog(
            show = true,
            initialTime = initialTime,
            onDismiss = onDismiss,
            onTimeSelected = onTimeSelected
        )
    }
}

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS no-op
}

// ===== APP LOCK: iOS LocalAuthentication Implementation =====
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberSecurityLockLauncher(): () -> Unit {
    return remember {
        {
            val context = LAContext()
            var error: NSError? = null

            val canEvaluate = context.canEvaluatePolicy(
                LAPolicyDeviceOwnerAuthentication,
                error = error?.ptr
            )

            if (!canEvaluate) {
                return@remember
            }

            val rootViewController = UIApplication.sharedApplication
                .keyWindow?.rootViewController as? UIViewController

            context.evaluatePolicy(
                policy = LAPolicyDeviceOwnerAuthentication,
                localizedReason = "Unlock Matrix 28",
                reply = { success, error ->
                    if (success) {
                        // Unlocked successfully
                    }
                }
            )
        }
    }
}
