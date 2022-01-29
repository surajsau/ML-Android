package `in`.surajsau.jisho.ui.chat

import `in`.surajsau.jisho.domain.models.chat.ChatAnnotation
import `in`.surajsau.jisho.domain.models.chat.ChatRowModel
import `in`.surajsau.jisho.ui.base.rememberExternalIntentHandler
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle

@Composable
fun ChatClickableText(
    annotatedString: AnnotatedString,
    annotationMaps: List<ChatAnnotation>,
    modifier: Modifier = Modifier
) {

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
                        is ChatAnnotation.Phone -> externalIntentHandler.openPhone(annotation.phone)
                        is ChatAnnotation.Email -> externalIntentHandler.openEmail(annotation.email)
                    }
                }
            }
        }
    )
}