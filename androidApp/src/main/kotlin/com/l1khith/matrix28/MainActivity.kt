package com.l1khith.matrix28

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Offload AdMob initialization to IO thread (Bug #20)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                com.google.android.gms.ads.MobileAds.initialize(this@MainActivity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        enableEdgeToEdge()

        val selectedTaskId = intent?.getStringExtra("selected_task_id")

        setContent {
            App()
        }

        // Only request notification permission on startup if required (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionsLauncher.launch(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS))
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}