package `in`.surajsau.jisho.ui.chat

import `in`.surajsau.jisho.base.Optional
import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.domain.chat.*
import `in`.surajsau.jisho.domain.models.chat.ChatDetails
import `in`.surajsau.jisho.domain.models.chat.ChatRowModel
import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SmartChatViewModelImpl @Inject constructor(
    private val sendMessage: SendMessage,
    private val fetchChatDetails: FetchChatDetails,
    private val fetchLatestMessage: FetchLatestMessage,
    private val checkEntityExtractorAvailability: CheckEntityExtractorAvailability,
): ViewModel(), SmartChatViewModel {

    private val userSwitchRandom = Random(1)

    private val _messages = MutableStateFlow(emptyList<ChatRowModel>())

    private val _chatDetails = MutableStateFlow<Optional<ChatDetails>>(Optional.Empty)

    private val _modelStatus = MutableStateFlow(MLKitModelStatus.NotDownloaded)

    private val _messageContainerModel = MutableStateFlow(SmartChatViewModel.MessageContainerModel())

    init {
        viewModelScope.launch {
            fetchLatestMessage.invoke()
                .onEach { chatRowModel ->
                    val messages = _messages.value.toMutableList()
                    _messages.value = messages.apply {
                        when {
                            chatRowModel is ChatRowModel.Message && chatRowModel.isLocal -> add(0, chatRowModel)
                            chatRowModel is ChatRowModel.Message && !chatRowModel.isLocal -> {
                                val existingTypingIndex = indexOfFirst { it is ChatRowModel.Typing }
                                if (existingTypingIndex == -1)
                                    add(0, chatRowModel)
                                else
                                    set(existingTypingIndex, chatRowModel)
                            }

                            else -> add(0, chatRowModel)
                        }
                    }
                }
                .flowOn(Dispatchers.IO)
                .collect()
        }

        viewModelScope.launch {
            fetchChatDetails.invoke()
                .flowOn(Dispatchers.IO)
                .collect { _chatDetails.value = Optional.of(it) }
        }

        viewModelScope.launch {
            checkEntityExtractorAvailability.invoke()
                .flowOn(Dispatchers.IO)
                .collect { _modelStatus.value = it }
        }
    }

    override val state: StateFlow<SmartChatViewModel.State>
        get() = combine(
            _messages,
            _chatDetails,
            _modelStatus,
            _messageContainerModel
        ) { messages, chatDetails, modelStatus, messageContainerModel ->
            val showLoader = modelStatus != MLKitModelStatus.Downloaded || chatDetails is Optional.Empty

            SmartChatViewModel.State(
                showLoader = showLoader,
                messages = messages,
                chatDetails = chatDetails,
                messageContainerModel = messageContainerModel
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SmartChatViewModel.State())

    override fun onEvent(event: SmartChatViewModel.Event) {
        when (event) {
            is SmartChatViewModel.Event.MessageTextChanged -> {
                val imageUrl = SmartChatViewModel.ImageUrlRegex.find(event.message)

                Log.e("SmartChat", "${imageUrl?.value}")

                _messageContainerModel.value = _messageContainerModel.value.copy(
                    isSendButtonEnabled = event.message.isNotEmpty(),
                    imageUrl = imageUrl?.value,
                    text = event.message
                )
            }

            is SmartChatViewModel.Event.SendMessageClicked -> {
                viewModelScope.launch {
                    val messageContainerModel = _messageContainerModel.value
                    sendMessage.invoke(
                        imageUrl = messageContainerModel.imageUrl,
                        message = messageContainerModel.text,
                        isLocal = messageContainerModel.currentUser == SmartChatViewModel.CurrentUser.LOCAL
                    )

                    _messageContainerModel.value = SmartChatViewModel.MessageContainerModel(
                        currentUser = SmartChatViewModel.CurrentUser.from(random = userSwitchRandom.nextFloat()),
                        isSendButtonEnabled = false,
                        imageUrl = null,
                        text = ""
                    )
                }
            }
        }
    }

}

interface SmartChatViewModel : SingleFlowViewModel<SmartChatViewModel.Event, SmartChatViewModel.State> {

    sealed class Event {
        data class MessageTextChanged(val message: String): Event()
        object SendMessageClicked: Event()
    }

    data class State(
        val showLoader: Boolean = false,
        val messages: List<ChatRowModel> = emptyList(),
        val chatDetails: Optional<ChatDetails> = Optional.Empty,
        val messageContainerModel: MessageContainerModel = MessageContainerModel(),
    )

    enum class CurrentUser {
        LOCAL, REMOTE;

        companion object {
            fun from(random: Float): CurrentUser = if (random < 0.5f) { LOCAL } else { REMOTE }
        }
    }

    data class MessageContainerModel(
        val currentUser: CurrentUser = CurrentUser.LOCAL,
        val isSendButtonEnabled: Boolean = false,
        val imageUrl: String? = null,
        val text: String = ""
    )

    companion object {
        val ImageUrlRegex = Regex("(https?:\\/\\/.*\\.(?:png|jpg|jpeg))")
    }
}

val LocalSmartChatViewModel = compositionLocalOf<SmartChatViewModel> {
    error("SmartChatViewModel factory not provided")
}