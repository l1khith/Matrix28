package com.l1khith.matrix28.utils

import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.l1khith.matrix28.data.AppContext

actual fun copyToClipboard(text: String) {
    try {
        val context = AppContext.get()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Calendar Event ICS", text)
        clipboard.setPrimaryClip(clip)
    } catch (_: Exception) {}
}

actual fun showPlatformToast(message: String) {
    try {
        val context = AppContext.get()
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {}
}

@Composable
actual fun PlatformTimePicker(
    show: Boolean,
    initialTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    if (!show) return
    val context = LocalContext.current
    var hour = 7
    var min = 0
    try {
        val clean = initialTime.replace(" Daily", "")
        val parts = clean.split(" ", ":")
        hour = parts.getOrNull(0)?.toIntOrNull() ?: 7
        min = parts.getOrNull(1)?.toIntOrNull() ?: 0
        if (clean.contains("PM", ignoreCase = true) && hour < 12) hour += 12
        if (clean.contains("AM", ignoreCase = true) && hour == 12) hour = 0
    } catch (_: Exception) {}

    DisposableEffect(show) {
        val dialog = TimePickerDialog(
            context,
            { _, h, m ->
                val amPm = if (h >= 12) "PM" else "AM"
                val hour12 = when {
                    h == 0 -> 12
                    h > 12 -> h - 12
                    else -> h
                }
                val formatted = "${hour12.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')} $amPm"
                onTimeSelected(formatted)
            },
            hour,
            min,
            false
        )
        dialog.setOnDismissListener { onDismiss() }
        dialog.show()
        onDispose {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }
}

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(enabled = enabled, onBack = onBack)
}

private fun Context.findFragmentActivity(): FragmentActivity? {
    var ctx = this
    while (ctx is android.content.ContextWrapper) {
        if (ctx is FragmentActivity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

// ===== APP LOCK: Android Biometric + PIN Implementation =====
@Composable
actual fun rememberSecurityLockLauncher(): () -> Unit {
    val context = LocalContext.current

    return remember(context) {
        {
            val activity = context.findFragmentActivity()
            if (activity == null) {
                showPlatformToast("App lock requires FragmentActivity")
                return@remember
            }

            val executor = ContextCompat.getMainExecutor(activity)
            val biometricManager = BiometricManager.from(activity)

            val canAuth = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )

            if (canAuth == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
                showPlatformToast("No biometric hardware available")
                return@remember
            }

            if (canAuth == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
                showPlatformToast("Please set up a screen lock in Settings first")
                return@remember
            }

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock Matrix 28")
                .setSubtitle("Verify your identity")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                .build()

            val biometricPrompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        showPlatformToast("Unlocked successfully")
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        showPlatformToast("Authentication failed: $errString")
                    }

                    override fun onAuthenticationFailed() {
                        showPlatformToast("Authentication failed")
                    }
                }
            )

            biometricPrompt.authenticate(promptInfo)
        }
    }
}
