package `in`.surajsau.jisho.domain.chat

import `in`.surajsau.jisho.data.ChatDataProvider
import `in`.surajsau.jisho.domain.models.chat.ChatDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchChatDetails @Inject constructor(
    private val chatDataProvider: ChatDataProvider
) {

    fun invoke(): Flow<ChatDetails> = chatDataProvider.fetchChatDetails()
}
