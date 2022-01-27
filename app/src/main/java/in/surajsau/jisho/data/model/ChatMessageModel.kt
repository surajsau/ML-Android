package `in`.surajsau.jisho.data.model

sealed class ChatMessageModel(val timeStamp: Long) {
    data class Typing(private val ts: Long): ChatMessageModel(timeStamp = ts)
    data class Message(val text: String, val isMe: Boolean, private val ts: Long): ChatMessageModel(timeStamp = ts)
}