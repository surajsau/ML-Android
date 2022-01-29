package `in`.surajsau.jisho.domain.chat

import `in`.surajsau.jisho.data.ChatDataProvider
import javax.inject.Inject

class SendPicture @Inject constructor(
    private val chatDataProvider: ChatDataProvider
) {

    suspend fun invoke(imageUrl: String, message: String?, isLocal: Boolean) {
        chatDataProvider.sendMessage(
            message = message ?: "",
            isMe = isLocal,
            imageUrl = imageUrl,
        )
    }
}