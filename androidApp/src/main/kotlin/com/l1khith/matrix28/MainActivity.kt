package com.l1khith.matrix28

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.l1khith.matrix28.ui.FixedCalendarApp
import com.l1khith.matrix28.viewmodel.FixedCalendarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var deepLinkId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fix Bug #3: Use applicationContext to avoid memory leaks
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                com.google.android.gms.ads.MobileAds.initialize(applicationContext)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        enableEdgeToEdge()

        // Fix Bug #4: Extract initial deep link task ID
        deepLinkId = intent?.getStringExtra("selected_task_id")

        setContent {
            App(initialTaskId = deepLinkId)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkId = intent.getStringExtra("selected_task_id")
    }
}

// Fix Bug #5: Preview UI directly to avoid ViewModelStoreOwner missing crash in previews
@Preview
@Composable
fun AppAndroidPreview() {
    MaterialTheme {
        FixedCalendarApp(viewModel = FixedCalendarViewModel())
    }
}