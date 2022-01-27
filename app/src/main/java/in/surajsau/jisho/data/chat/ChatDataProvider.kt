package `in`.surajsau.jisho.data.chat

import `in`.surajsau.jisho.data.model.ChatMessageModel
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

class ChatDataProviderImpl @Inject constructor(
    private val chatApi: ChatApi,
): ChatDataProvider {

    override val latestMessage: Channel<ChatMessageModel> = Channel()

    override suspend fun sendMessage(text: String) {
        val message = ChatMessageModel.Message(
            text = text,
            isMe = true,
            ts = System.currentTimeMillis()
        )

        latestMessage.trySend(message)
    }

    override suspend fun fetchRandomMessage() {
        val timeStamp = System.currentTimeMillis()

        latestMessage.trySend(ChatMessageModel.Typing(ts = timeStamp))

        val response = chatApi.randomKanye("https://api.kanye.rest/")
        val message = ChatMessageModel.Message(
            text = response.quote,
            isMe = false,
            ts = timeStamp
        )

        latestMessage.trySend(message)
    }
}

interface ChatDataProvider {

    val latestMessage: Channel<ChatMessageModel>

    suspend fun sendMessage(message: String)
    suspend fun fetchRandomMessage()
}