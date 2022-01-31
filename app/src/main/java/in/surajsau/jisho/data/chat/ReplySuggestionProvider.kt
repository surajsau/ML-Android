package `in`.surajsau.jisho.data.chat

import `in`.surajsau.jisho.data.model.ChatMessageModel
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.smartreply.*
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

class ReplySuggestionProviderImpl @Inject constructor() : ReplySuggestionProvider {

    private val smartReplyGenerator by lazy {
        SmartReply.getClient(SmartReplyGeneratorOptions.Builder().build())
    }

    private val conversation = mutableListOf<TextMessage>()

    override val suggestions: Channel<List<SmartReplySuggestion>> = Channel()

    override suspend fun addMessage(message: ChatMessageModel.Message) {
        conversation.add(
            if (message.isMe)
                TextMessage.createForLocalUser(message.text, message.timeStamp)
            else
                TextMessage.createForRemoteUser(message.text, message.timeStamp, "1")
        )

        if (!message.isMe) {
            val task = smartReplyGenerator.suggestReplies(conversation)
            val result = Tasks.await(task)

            suggestions.trySend(result.suggestions)
        }
    }

    override fun clearConversation() {
        conversation.clear()
    }
}

interface ReplySuggestionProvider {

    val suggestions: Channel<List<SmartReplySuggestion>>

    suspend fun addMessage(message: ChatMessageModel.Message)

    fun clearConversation()
}