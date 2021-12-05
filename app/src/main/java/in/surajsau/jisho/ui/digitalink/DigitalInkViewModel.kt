package `in`.surajsau.jisho.ui.digitalink

import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.domain.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DigitalInkViewModelImpl @Inject constructor(
    downloadDigitalInkIfNeeded: DownloadDigitalInkIfNeeded,
    downloadTranslatorIfNeeded: DownloadTranslatorIfNeeded,
    consumePrediction: ConsumePrediction,
    consumeTranslation: ConsumeTranslation,
    private val fetchTranslation: FetchTranslation,
    private val recordCoordinate: RecordCoordinate,
    private val finishedRecording: FinishedRecording,
    private val closeMLKit: CloseMLKit,
): ViewModel(), DigitalInkViewModel {

    private var finishRecordingJob: Job? = null

    private val _digitalInkModelStatus = downloadDigitalInkIfNeeded.invoke()
        .stateIn(viewModelScope, SharingStarted.Lazily, MLKitModelStatus.NotDownloaded)

    private val _translatorModelStatus = downloadTranslatorIfNeeded.invoke()
        .stateIn(viewModelScope, SharingStarted.Lazily, MLKitModelStatus.NotDownloaded)

    private val _predictions = consumePrediction.invoke()
        .onEach {
            if (it.isEmpty())
                return@onEach

            setFinalText(text = _finalText.value.plus(it[0]))
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _translation = consumeTranslation.invoke()
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    private val _finalText = MutableStateFlow<String>("")
    private val _resetCanvas = MutableStateFlow<Boolean>(false)

    override val state: StateFlow<DigitalInkViewModel.State>
        get() = combine(
            _digitalInkModelStatus,
            _translatorModelStatus,
            _resetCanvas,
            _predictions,
            _translation,
            _finalText
        ) { result ->
            val digitalInkModelStatus = result[0] as MLKitModelStatus
            val translatorModelStatus = result[1] as MLKitModelStatus
            val resetCanvas = result[2] as Boolean
            val predictions = result[3] as List<String>
            val translation = result[4] as String
            val finalText = result[5] as String
            val areModelsDownloaded = digitalInkModelStatus == MLKitModelStatus.Downloaded && translatorModelStatus == MLKitModelStatus.Downloaded

            DigitalInkViewModel.State(
                resetCanvas = resetCanvas,
                showModelStatusProgress = !areModelsDownloaded,
                finalText = finalText,
                translation = translation,
                predictions = predictions
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, DigitalInkViewModel.State())

    override fun onEvent(event: DigitalInkViewModel.Event) {

        when (event) {
            is DigitalInkViewModel.Event.Pointer -> {

                when (val drawEvent = event.event) {
                    is DrawEvent.Down -> {
                        this.finishRecordingJob?.cancel()
                        _resetCanvas.value = false

                        recordCoordinate.invoke(drawEvent.x, drawEvent.y)
                    }

                    is DrawEvent.Move -> {
                        recordCoordinate.invoke(drawEvent.x, drawEvent.y)
                    }

                    is DrawEvent.Up -> {
                        this.finishRecordingJob = viewModelScope.launch {
                            delay(DEBOUNCE_INTERVAL)
                            _resetCanvas.value = true
                            finishedRecording.invoke()
                        }
                    }
                }
            }

            is DigitalInkViewModel.Event.OnStop -> closeMLKit.invoke()

            is DigitalInkViewModel.Event.TextChanged -> {
                setFinalText(event.text)
            }

            is DigitalInkViewModel.Event.PredictionSelected -> {
                setFinalText(text = _finalText.value.dropLast(1).plus(event.prediction))
            }
        }
    }

    private fun setFinalText(text: String) {
        _finalText.value = text

        if (text.isNotEmpty())
            fetchTranslation.invoke(text)
    }

    companion object {
        private const val DEBOUNCE_INTERVAL = 1000L
    }
}

interface DigitalInkViewModel: SingleFlowViewModel<DigitalInkViewModel.Event, DigitalInkViewModel.State> {

    data class State(
        val resetCanvas: Boolean = false,
        val showModelStatusProgress: Boolean = false,
        val finalText: String = "",
        val translation: String = "",
        val predictions: List<String> = emptyList(),
    )

    sealed class Event {
        data class TextChanged(val text: String): Event()
        data class Pointer(val event: DrawEvent): Event()
        data class PredictionSelected(val prediction: String): Event()

        object OnStop: Event()
    }
}

val LocalDigitalInkViewModel = compositionLocalOf<DigitalInkViewModel>{ error("LocalDigitalViewModelFactory not provided") }

@Composable
fun provideDigitalInkViewModel(viewModelProvider: @Composable () -> DigitalInkViewModel)
    = LocalDigitalInkViewModel provides viewModelProvider.invoke()