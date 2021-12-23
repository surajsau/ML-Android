package `in`.surajsau.jisho.ui.gpt

import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.data.chat.ChatProvider
import `in`.surajsau.jisho.data.gpt.GPTProvider
import `in`.surajsau.jisho.data.model.ChatMessage
import `in`.surajsau.jisho.data.model.Suggestion
import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatSuggestionViewModelImpl @Inject constructor(
    private val chatProvider: ChatProvider,
    private val gptProvider: GPTProvider
): ViewModel(), ChatSuggestionViewModel {

    init {
        viewModelScope.launch {
            chatProvider.latestMessage
                .receiveAsFlow()
                .onEach { message ->
                    val messages = _messages.value.toMutableList()
                    _messages.value = messages.apply {
                        val existingMessageIndex = this.indexOfLast { it.timeStamp == message.timeStamp }
                        if (existingMessageIndex == -1)
                            add(0, message)
                        else
                            add(existingMessageIndex, message)
                    }

                    if (message is ChatMessage.Message && !message.isMe) {
                        gptProvider.generate(text = message.text, maxLength = 10)
                    }
                }
                .flowOn(Dispatchers.IO)
                .collect()
        }
    }

    private val _gptModelStatus = gptProvider.loadModel()
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Lazily, MLKitModelStatus.CheckingDownload)

    private val _showSuggestionBlock = MutableStateFlow(false)

    private val _messages = MutableStateFlow(emptyList<ChatMessage>())

    private val _currentMessage = MutableStateFlow("")

    private val _suggestion = gptProvider.suggestion
        .receiveAsFlow()
        .onEach { _showSuggestionBlock.value = true }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    override val state: StateFlow<ChatSuggestionViewModel.State>
        get() = combine(
            _gptModelStatus,
            _messages,
            _currentMessage,
            _suggestion,
            _showSuggestionBlock,
        ) { modelStatus, messages, currentMessage, suggestion, showSuggestionBlock ->
            val isModelReady = modelStatus == MLKitModelStatus.Downloaded
            val textSuggestion = (suggestion as? Suggestion.Message)?.value ?: ""
            val showSuggestionLoader = suggestion is Suggestion.Interpreting

            ChatSuggestionViewModel.State(
                showLoader = !isModelReady,
                isSendButtonEnabled = true,
                messages = messages,
                currentMessage = currentMessage,
                textSuggestion = textSuggestion,
                showSuggestionBlock = showSuggestionBlock,
                showSuggestionLoader = showSuggestionLoader,
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ChatSuggestionViewModel.State())

    override fun onEvent(event: ChatSuggestionViewModel.Event) {
        when (event) {
            is ChatSuggestionViewModel.Event.SendMessage -> {
                val message = _currentMessage.value
                _currentMessage.value = ""

                viewModelScope.launch {
                    chatProvider.sendMessage(message)
                    chatProvider.fetchRandomMessage()
                }
            }

            is ChatSuggestionViewModel.Event.AcceptSuggestion -> {
                _showSuggestionBlock.value = false
                _currentMessage.value = _currentMessage.value.plus(event.suggestion)
            }

            is ChatSuggestionViewModel.Event.TextChange -> {
                _currentMessage.value = event.text
            }
        }
    }

}

interface ChatSuggestionViewModel : SingleFlowViewModel<ChatSuggestionViewModel.Event, ChatSuggestionViewModel.State> {

    sealed class Event {
        data class AcceptSuggestion(val suggestion: String): Event()
        object SendMessage: Event()
        data class TextChange(val text: String): Event()
    }

    data class State(
        val showLoader: Boolean = false,
        val isSendButtonEnabled: Boolean = false,
        val currentMessage: String = "",
        val messages: List<ChatMessage> = emptyList(),
        val showSuggestionBlock: Boolean = false,
        val showSuggestionLoader: Boolean = false,
        val textSuggestion: String = ""
    )
}

val LocalChatSuggestionViewModel = compositionLocalOf<ChatSuggestionViewModel> {
    error("ChatSuggestionModel factory not provided")
}