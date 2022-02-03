package `in`.surajsau.jisho.domain.chat

import `in`.surajsau.jisho.data.ChatDataProvider
import javax.inject.Inject

class SendMessage @Inject constructor(
    private val chatDataProvider: ChatDataProvider
) {

    suspend fun invoke(imageUrl: String?, message: String, isLocal: Boolean) {
        chatDataProvider.sendMessage(
            message = message,
            isMe = isLocal,
            imageUrl = imageUrl,
        )
    }
}