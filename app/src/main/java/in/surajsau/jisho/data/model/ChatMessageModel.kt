package `in`.surajsau.jisho.data.model

sealed class ChatMessageModel(val timeStamp: Long) {
    data class Typing(private val ts: Long): ChatMessageModel(timeStamp = ts)
    data class Message(
        val id: Int,
        val text: String,
        val isMe: Boolean,
        private val ts: Long,
        val imageUrl: String?,
    ): ChatMessageModel(timeStamp = ts)
}