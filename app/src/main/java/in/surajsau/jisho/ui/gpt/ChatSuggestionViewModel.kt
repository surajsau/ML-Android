package `in`.surajsau.jisho.ui.gpt

import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.data.chat.ChatProvider
import `in`.surajsau.jisho.data.gpt.GPTProvider
import `in`.surajsau.jisho.data.model.ChatMessage
import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
                }
                .flowOn(Dispatchers.IO)
                .collect()
        }
    }

    private val _gptModelStatus = gptProvider.loadModel()
        .stateIn(viewModelScope, SharingStarted.Lazily, MLKitModelStatus.CheckingDownload)

    private var predictionJob: Job? = null

    private val _messages = MutableStateFlow(emptyList<ChatMessage>())

    private val _showSuggestion = MutableStateFlow(false)

    private val _currentMessage = MutableStateFlow("")

    private val _textSuggestion = MutableStateFlow("")

    override val state: StateFlow<ChatSuggestionViewModel.State>
        get() = combine(
            _gptModelStatus,
            _messages,
            _currentMessage,
            _textSuggestion,
            _showSuggestion
        ) { modelStatus, messages, currentMessage, textSuggestion, showSuggestion ->
            val isModelReady = modelStatus == MLKitModelStatus.Downloaded

            ChatSuggestionViewModel.State(
                showLoader = !isModelReady,
                isSendButtonEnabled = !isModelReady,
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
                val text = event.text

                if (text.split(" ").size >= 3) {
                    predictionJob?.cancel()

                    predictionJob = viewModelScope.launch {
                        gptProvider.generate(text = text, maxLength = 20)
                            .flowOn(Dispatchers.IO)
                            .collect { suggestionText ->
                                _showSuggestion.value = suggestionText.isNotEmpty()
                                _textSuggestion.value = suggestionText
                            }
                    }
                }

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