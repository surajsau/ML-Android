package `in`.surajsau.jisho.ui.chat

import `in`.surajsau.jisho.base.Optional
import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.domain.chat.*
import `in`.surajsau.jisho.domain.models.User
import `in`.surajsau.jisho.domain.models.chat.ChatDetails
import `in`.surajsau.jisho.domain.models.chat.ChatRowModel
import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
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
    private val sendPicture: SendPicture,
    private val fetchChatDetails: FetchChatDetails,
    private val fetchLatestMessage: FetchLatestMessage,
    private val checkEntityExtractorAvailability: CheckEntityExtractorAvailability,
): ViewModel(), SmartChatViewModel {

    private val userSwitchRandom = Random(1)

    private val _currentUser = MutableStateFlow(SmartChatViewModel.CurrentUser.LOCAL)

    private val _messages = MutableStateFlow(emptyList<ChatRowModel>())

    private val _chatDetails = MutableStateFlow<Optional<ChatDetails>>(Optional.Empty)

    private val _modelStatus = MutableStateFlow(MLKitModelStatus.NotDownloaded)

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
            _currentUser,
            _messages,
            _chatDetails,
            _modelStatus,
        ) { currentUser, messages, chatDetails, modelStatus ->
            val showLoader = modelStatus != MLKitModelStatus.Downloaded || chatDetails is Optional.Empty

            SmartChatViewModel.State(
                showLoader = showLoader,
                currentUser = currentUser,
                messages = messages,
                chatDetails = chatDetails,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SmartChatViewModel.State())

    override fun onEvent(event: SmartChatViewModel.Event) {
        when (event) {
            is SmartChatViewModel.Event.SendMessage -> {
                viewModelScope.launch {
                    val currentUser = _currentUser.value
                    sendMessage.invoke(
                        message = event.message,
                        isLocal = currentUser == SmartChatViewModel.CurrentUser.LOCAL
                    )

                    _currentUser.value = SmartChatViewModel.CurrentUser.from(random = userSwitchRandom.nextFloat())
                }
            }

            is SmartChatViewModel.Event.SendPicture -> {
                viewModelScope.launch {
                    val currentUser = _currentUser.value
                    sendPicture.invoke(
                        imageUrl = event.imageUrl,
                        message = event.message,
                        isLocal = currentUser == SmartChatViewModel.CurrentUser.LOCAL
                    )

                    _currentUser.value = SmartChatViewModel.CurrentUser.from(random = userSwitchRandom.nextFloat())
                }
            }
        }
    }

}

interface SmartChatViewModel : SingleFlowViewModel<SmartChatViewModel.Event, SmartChatViewModel.State> {

    sealed class Event {
        data class SendMessage(val message: String): Event()

        data class SendPicture(
            val imageUrl: String,
            val message: String
        ): Event()
    }

    data class State(
        val showLoader: Boolean = false,
        val currentUser: CurrentUser = CurrentUser.LOCAL,
        val messages: List<ChatRowModel> = emptyList(),
        val chatDetails: Optional<ChatDetails> = Optional.Empty,
    )

    enum class CurrentUser {
        LOCAL, REMOTE;

        companion object {
            fun from(random: Float): CurrentUser = if (random < 0.5f) { LOCAL } else { REMOTE }
        }
    }
}

val LocalSmartChatViewModel = compositionLocalOf<SmartChatViewModel> {
    error("SmartChatViewModel factory not provided")
}