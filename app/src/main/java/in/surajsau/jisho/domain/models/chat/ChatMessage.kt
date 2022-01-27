package `in`.surajsau.jisho.domain.models.chat

sealed class ChatRowModel {
    object Typing: ChatRowModel()

    data class Message(
        val value: String,
        val timestamp: String,
        val isLocal: Boolean
    ): ChatRowModel()
}
