package `in`.surajsau.jisho.ui.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.soywiz.klock.DateTime

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberExternalIntentHandler(): ExternalIntentHandler {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.CALL_PHONE)
    return remember { ExternalIntentHandler(context) }
}

class ExternalIntentHandler constructor(
    private val context: Context
) {

    fun openMap(location: String) {
        val gmmIntentUri =
            Uri.parse("geo:0,0?q=$location")
        val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun openAlarm(message: String, timestamp: Long) {
        val dateTime = DateTime.invoke(timestamp)
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_HOUR, dateTime.hours)
            putExtra(AlarmClock.EXTRA_MINUTES, dateTime.minutes)
        }

        context.startActivity(intent)
    }

    fun openPhone(phone: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phone")
        }

        if (intent.resolveActivity(context.packageManager) != null)
            context.startActivity(intent)
    }

    fun openEmail(address: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, address)
        }

        context.startActivity(Intent.createChooser(intent, "Send Email"))
    }
}