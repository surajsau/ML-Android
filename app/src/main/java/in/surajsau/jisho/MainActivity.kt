package `in`.surajsau.jisho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import `in`.surajsau.jisho.ui.MLApp
import android.content.Intent
import android.net.Uri
import android.provider.Settings

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MLApp(
            navigateToSettings = {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        ) }
    }
}