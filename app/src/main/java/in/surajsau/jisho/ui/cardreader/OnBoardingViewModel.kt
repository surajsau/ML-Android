package `in`.surajsau.jisho.ui.cardreader

import `in`.surajsau.jisho.base.Optional
import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.domain.cardreader.GetBackCardDetails
import `in`.surajsau.jisho.domain.cardreader.GetFrontCardDetails
import `in`.surajsau.jisho.domain.models.CardDetails
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardReaderViewModelImpl @Inject constructor(
    private val getBackCardDetails: GetBackCardDetails,
    private val getFrontCardDetails: GetFrontCardDetails,
): ViewModel(), CardReaderViewModel {

    private val _frontDetails = MutableStateFlow<Optional<CardDetails.Front>>(Optional.Empty)
    private val _backDetails = MutableStateFlow<Optional<CardDetails.Back>>(Optional.Empty)

    private val _cardReaderMode = MutableStateFlow(CardReaderViewModel.CardReaderMode.FrontCapture)

    private val _currentScreen = MutableStateFlow<CardReaderViewModel.Screen>(CardReaderViewModel.Screen.Intro)

    private val _showLoader = MutableStateFlow(false)

    override val state: StateFlow<CardReaderViewModel.State>
        get() = combine(
            _frontDetails,
            _backDetails,
            _cardReaderMode,
            _currentScreen,
            _showLoader,
        ) { frontDetails, backDetails, cardReaderMode, currentScreen, showLoader ->
            val instruction = when (cardReaderMode) {
                CardReaderViewModel.CardReaderMode.FrontCapture -> "Show front of the Card"
                CardReaderViewModel.CardReaderMode.BackCapture -> "Show back of the Card"

                else -> ""
            }

            val screen = when (cardReaderMode) {
                CardReaderViewModel.CardReaderMode.Result -> CardReaderViewModel.Screen.FilledDetails(
                    front = frontDetails.getValue(),
                    back = backDetails.getValue(),
                )

                else -> currentScreen
            }

            CardReaderViewModel.State(
                instruction = instruction,
                showLoader = showLoader,
                screen = screen,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), CardReaderViewModel.State())

    override fun onEvent(event: CardReaderViewModel.Event) {
        when (event) {
            is CardReaderViewModel.Event.CameraResultReceived -> {
                val cardReaderMode = _cardReaderMode.value

                _showLoader.value = true

                viewModelScope.launch {
                    when (cardReaderMode) {
                        CardReaderViewModel.CardReaderMode.FrontCapture -> {
                            getFrontCardDetails.invoke(fileName = event.fileName)
                                .flowOn(Dispatchers.IO)
                                .collect {
                                    _frontDetails.value = Optional.Some(it)
                                    _cardReaderMode.value = CardReaderViewModel.CardReaderMode.BackCapture
                                }
                        }

                        CardReaderViewModel.CardReaderMode.BackCapture -> {
                            getBackCardDetails.invoke(fileName = event.fileName)
                                .flowOn(Dispatchers.IO)
                                .collect {
                                    _backDetails.value = Optional.Some(it)
                                    _cardReaderMode.value = CardReaderViewModel.CardReaderMode.Result
                                }
                        }
                    }
                }
            }

            is CardReaderViewModel.Event.ScreenSelected -> {
                _currentScreen.value = event.screen
            }

            is CardReaderViewModel.Event.ConfirmClicked -> {

            }
        }
    }

}

interface CardReaderViewModel : SingleFlowViewModel<CardReaderViewModel.Event, CardReaderViewModel.State> {

    sealed class Event{
        data class CameraResultReceived(val fileName: String): Event()
        object OnPermissionDenied: Event()
        data class ScreenSelected(val screen: Screen): Event()
        object ConfirmClicked: Event()
    }

    data class State(
        val showLoader: Boolean = false,
        val instruction: String = "",
        val screen: Screen = Screen.Intro,
    )

    enum class CardReaderMode {
        FrontCapture,
        BackCapture,
        Result,
    }

    sealed class Screen(val title: String) {
        object Intro: Screen("")
        object CardReader: Screen("")
        object EmptyDetails: Screen("Enter details")
        data class FilledDetails(
            val front: CardDetails.Front,
            val back: CardDetails.Back,
        ): Screen("Confirm details")
    }
}

val LocalOnboardingViewModel = compositionLocalOf<CardReaderViewModel> {
    error("Factory for CardReaderViewModel is not implemented")
}