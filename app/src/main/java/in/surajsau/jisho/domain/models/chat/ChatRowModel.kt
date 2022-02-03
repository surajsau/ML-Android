package `in`.surajsau.jisho.domain.models.chat

import androidx.compose.ui.text.AnnotatedString

sealed interface ChatRowModel {

    val isLocal: Boolean

    object Typing: ChatRowModel {
        override val isLocal: Boolean = false
    }

    data class Message(
        val value: AnnotatedString,
        val timestamp: String,
        override val isLocal: Boolean,
        val annotationMaps: List<ChatAnnotation>
    ): ChatRowModel

    data class PictureMessage(
        val imageUrl: String,
        val timestamp: String,
        override val isLocal: Boolean,
        val message: AnnotatedString,
        val annotationMaps: List<ChatAnnotation>
    ): ChatRowModel
}
