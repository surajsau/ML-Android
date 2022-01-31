package `in`.surajsau.jisho.domain.models.chat

import androidx.compose.ui.text.AnnotatedString

sealed class ChatRowModel {
    object Typing: ChatRowModel()

    data class Message(
        val value: AnnotatedString,
        val timestamp: String,
        val isLocal: Boolean,
        val annotationMaps: List<ChatAnnotation>
    ): ChatRowModel()

    data class PictureMessage(
        val imageUrl: String,
        val timestamp: String,
        val isLocal: Boolean,
        val message: AnnotatedString,
        val annotationMaps: List<ChatAnnotation>
    ): ChatRowModel()
}
