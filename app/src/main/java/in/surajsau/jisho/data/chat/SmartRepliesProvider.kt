package `in`.surajsau.jisho.data.chat

import `in`.surajsau.jisho.data.model.ChatMessageModel
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.smartreply.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SmartRepliesProviderImpl @Inject constructor() : SmartRepliesProvider {

    private val smartReplyGenerator by lazy {
        SmartReply.getClient(SmartReplyGeneratorOptions.Builder().build())
    }

    private val conversation = mutableListOf<TextMessage>()

    override suspend fun getSuggestions(message: ChatMessageModel.Message): List<SmartReplySuggestion> {
        return withContext(Dispatchers.IO) {
            conversation.add(
                if (message.isMe)
                    TextMessage.createForLocalUser(message.text, message.timeStamp)
                else
                    TextMessage.createForRemoteUser(message.text, message.timeStamp, "1")
            )

            val task = smartReplyGenerator.suggestReplies(conversation)
            val result = Tasks.await(task)

            result.suggestions
        }
    }

    override fun clearConversation() {
        conversation.clear()
    }
}

interface SmartRepliesProvider {

    suspend fun getSuggestions(message: ChatMessageModel.Message): List<SmartReplySuggestion>

    fun clearConversation()
}