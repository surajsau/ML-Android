package `in`.surajsau.jisho.ui.cardreader

import `in`.surajsau.jisho.base.Optional
import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.domain.cardreader.GetCardDetails
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
    private val getCardDetails: GetCardDetails,
): ViewModel(), CardReaderViewModel {

    private val _cardDetails = MutableStateFlow<Optional<CardDetails>>(Optional.Empty)

    private val _screenMode = MutableStateFlow(CardReaderViewModel.ScreenMode.FrontCapture)

    override val state: StateFlow<CardReaderViewModel.State>
        get() = combine(
            _cardDetails,
            _screenMode
        ) { cardDetails, screenMode ->
            val instruction = when (screenMode) {
                CardReaderViewModel.ScreenMode.FrontCapture -> "Show front of the Card"
                CardReaderViewModel.ScreenMode.BackCapture -> "Show back of the Card"
                else -> ""
            }

            val showLoading = screenMode == CardReaderViewModel.ScreenMode.Processing

            val cardDetailsDialogMode = when (cardDetails) {
                is Optional.Some -> CardReaderViewModel.CardDetailsDialogMode.Show(cardDetails.getValue())
                is Optional.Empty -> CardReaderViewModel.CardDetailsDialogMode.Hide
            }

            CardReaderViewModel.State(
                cardDetailsDialogMode = cardDetailsDialogMode,
                instruction = instruction,
                showLoader = showLoading,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), CardReaderViewModel.State())

    override fun onEvent(event: CardReaderViewModel.Event) {
        when (event) {
            is CardReaderViewModel.Event.CameraResultReceived -> {
                _screenMode.value = CardReaderViewModel.ScreenMode.Processing

                viewModelScope.launch {
                    getCardDetails.invoke(fileName = event.fileName)
                        .flowOn(Dispatchers.IO)
                        .collect { _cardDetails.value = Optional.Some(it) }
                }
            }
        }
    }

}

interface CardReaderViewModel : SingleFlowViewModel<CardReaderViewModel.Event, CardReaderViewModel.State> {

    sealed class Event{
        data class CameraResultReceived(val fileName: String): Event()
        object OnPermissionDenied: Event()
    }

    data class State(
        val showLoader: Boolean = false,
        val cardDetailsDialogMode: CardDetailsDialogMode = CardDetailsDialogMode.Hide,
        val instruction: String = "",
    )

    enum class ScreenMode {
        FrontCapture,
        BackCapture,
        Processing,
        Result
    }

    sealed class CardDetailsDialogMode {
        data class Show(val cardDetails: CardDetails): CardDetailsDialogMode()
        object Hide: CardDetailsDialogMode()
    }
}

val LocalCardReaderViewModel = compositionLocalOf<CardReaderViewModel> {
    error("Factory for CardReaderViewModel is not implemented")
}