package `in`.surajsau.jisho.ui.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.soywiz.klock.DateTime

@Composable
fun rememberExternalIntentHandler(): ExternalIntentHandler {
    val context = LocalContext.current
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

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun openPhone(phone: String) {

    }

    fun openEmail(address: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, address)
        }

        context.startActivity(Intent.createChooser(intent, "Send Email"))
    }
}