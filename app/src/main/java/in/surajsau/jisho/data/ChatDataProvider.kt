package `in`.surajsau.jisho.data

import `in`.surajsau.jisho.data.model.ChatMessageModel
import `in`.surajsau.jisho.domain.models.chat.ChatDetails
import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random

class ChatDataProviderImpl @Inject constructor(): ChatDataProvider {

    private val randomDelay = Random(300L)

    override val latestMessage: Channel<ChatMessageModel> = Channel()

    override suspend fun sendMessage(
        text: String,
        imageUrl: String?,
        isMe: Boolean,
    ) {
        val ts = System.currentTimeMillis()
        val id = (ts/1000).toInt()
        val message = ChatMessageModel.Message(
            id = id,
            text = text,
            isMe = isMe,
            ts = ts,
            imageUrl = imageUrl,
        )

        // add random delay to act as an api
        if (!isMe) {
            latestMessage.trySend(ChatMessageModel.Typing(ts = ts))
            delay(randomDelay.nextLong(300L, 2000L))
        }

        latestMessage.trySend(message)
    }

    override fun fetchChatDetails(): Flow<ChatDetails> = flow {
        emit(
            ChatDetails(
                chatName = "レオレウス",
                chatIconUrl = "https://i.pinimg.com/originals/76/99/4b/76994b84e7e7f53a4cf1a5a4c52736d4.jpg"
            )
        )
    }
}

interface ChatDataProvider {

    val latestMessage: Channel<ChatMessageModel>

    suspend fun sendMessage(
        message: String,
        imageUrl: String? = null,
        isMe: Boolean
    )

    fun fetchChatDetails(): Flow<ChatDetails>
}