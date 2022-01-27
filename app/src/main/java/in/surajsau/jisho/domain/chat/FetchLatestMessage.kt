package `in`.surajsau.jisho.domain.chat

import `in`.surajsau.jisho.data.chat.ChatDataProvider
import `in`.surajsau.jisho.data.model.ChatMessageModel
import `in`.surajsau.jisho.domain.models.chat.ChatRowModel
import com.soywiz.klock.DateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class FetchLatestMessage @Inject constructor(
    private val chatDataProvider: ChatDataProvider,
) {

    fun invoke(): Flow<ChatRowModel> {
        // 9573789389
        return chatDataProvider.latestMessage
            .receiveAsFlow()
            .map {
                when (it) {
                    is ChatMessageModel.Message -> ChatRowModel.Message(
                        value = it.text,
                        isLocal = it.isMe,
                        timestamp = DateTime.invoke(it.timeStamp).toString("HH:mm")
                    )

                    is ChatMessageModel.Typing -> ChatRowModel.Typing
                }
            }
    }
}