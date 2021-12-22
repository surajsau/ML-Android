package `in`.surajsau.jisho.ui.gpt

import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.data.chat.ChatProvider
import `in`.surajsau.jisho.data.gpt.GPTProvider
import `in`.surajsau.jisho.data.model.ChatMessage
import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
                    _messages.value = _messages.value.toMutableList().apply {
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

    private val _messages = MutableStateFlow(emptyList<ChatMessage>())

    private val _currentMessage = MutableStateFlow("")

    private val _textSuggestion = gptProvider.suggestion
        .receiveAsFlow()
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    override val state: StateFlow<ChatSuggestionViewModel.State>
        get() = combine(
            _gptModelStatus,
            _messages,
            _currentMessage,
            _textSuggestion,
        ) { modelStatus, messages, currentMessage, textSuggestion ->
            val isModelReady = modelStatus == MLKitModelStatus.Downloaded
            val showSuggestion = textSuggestion.isNotEmpty()

            ChatSuggestionViewModel.State(
                showLoader = !isModelReady,
                isSendButtonEnabled = true,
                messages = messages,
                currentMessage = currentMessage,
                textSuggestion = textSuggestion,
                showSuggestion = showSuggestion,
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
        val showSuggestion: Boolean = false,
        val textSuggestion: String = ""
    )
}

val LocalChatSuggestionViewModel = compositionLocalOf<ChatSuggestionViewModel> {
    error("ChatSuggestionModel factory not provided")
}