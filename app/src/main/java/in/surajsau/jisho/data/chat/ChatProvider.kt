package `in`.surajsau.jisho.data.chat

import `in`.surajsau.jisho.data.model.ChatMessage
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

class ChatProviderImpl @Inject constructor(
    private val chatApi: ChatApi,
): ChatProvider {

    override val latestMessage: Channel<ChatMessage> = Channel()

    override suspend fun sendMessage(text: String) {
        val message = ChatMessage.Message(
            text = text,
            isMe = true,
            ts = System.currentTimeMillis()
        )

        latestMessage.trySend(message)
    }

    override suspend fun fetchRandomMessage() {
        val timeStamp = System.currentTimeMillis()

        latestMessage.trySend(ChatMessage.Typing(ts = timeStamp))

        val response = chatApi.randomKanye("https://api.kanye.rest/")
        val message = ChatMessage.Message(
            text = response.quote,
            isMe = false,
            ts = timeStamp
        )

        latestMessage.trySend(message)
    }
}

interface ChatProvider {

    val latestMessage: Channel<ChatMessage>

    suspend fun sendMessage(message: String)
    suspend fun fetchRandomMessage()
}