package `in`.surajsau.jisho.data.model

sealed class ChatMessage(val timeStamp: Long) {
    data class Typing(private val ts: Long): ChatMessage(timeStamp = ts)
    data class Message(val text: String, val isMe: Boolean, private val ts: Long): ChatMessage(timeStamp = ts)
}