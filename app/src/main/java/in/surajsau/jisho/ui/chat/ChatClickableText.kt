package `in`.surajsau.jisho.ui.chat

import `in`.surajsau.jisho.domain.models.chat.ChatAnnotation
import `in`.surajsau.jisho.domain.models.chat.ChatRowModel
import `in`.surajsau.jisho.ui.base.rememberExternalIntentHandler
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatClickableText(
    annotatedString: AnnotatedString,
    annotationMaps: List<ChatAnnotation>,
    modifier: Modifier = Modifier
) {
    val callPermissionState = rememberPermissionState(permission = android.Manifest.permission.CALL_PHONE)

    val externalIntentHandler = rememberExternalIntentHandler()

    ClickableText(
        modifier = modifier,
        text = annotatedString,
        style = TextStyle(color = Color.White),
        onClick = { offset ->
            annotationMaps.forEach { annotation ->
                val tag = annotation.tag
                annotatedString.getStringAnnotations(tag = tag, start = offset, end = offset).firstOrNull()?.let {
                    when (annotation) {
                        is ChatAnnotation.Reminder -> externalIntentHandler.openAlarm(message = it.item, timestamp = annotation.timeStamp)
                        is ChatAnnotation.Address -> externalIntentHandler.openMap(annotation.address)
                        is ChatAnnotation.Phone -> {
                            when {
                                callPermissionState.hasPermission -> externalIntentHandler.openPhone(annotation.phone)
                                else -> callPermissionState.launchPermissionRequest()
                            }

                        }
                        is ChatAnnotation.Email -> externalIntentHandler.openEmail(annotation.email)
                    }
                }
            }
        }
    )
}