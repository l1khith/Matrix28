package com.l1khith.matrix28

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.l1khith.matrix28.ui.theme.MatrixTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private var deepLinkIdState by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                MobileAds.initialize(applicationContext) { status ->
                    Log.d("MainActivity", "AdMob initialized: ${status.adapterStatusMap}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "AdMob initialization error", e)
            }
        }

        enableEdgeToEdge()

        deepLinkIdState = intent?.getStringExtra("selected_task_id")

        setContent {
            val deepLinkId = rememberSaveable { mutableStateOf(deepLinkIdState) }
            App(
                initialTaskId = deepLinkId.value,
                onExitApp = { finish() }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkIdState = intent.getStringExtra("selected_task_id")
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    MatrixTheme {
        Text("Matrix 28 Preview")
    }
}