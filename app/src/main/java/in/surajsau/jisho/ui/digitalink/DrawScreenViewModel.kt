package `in`.surajsau.jisho.ui.digitalink

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawScreenViewModel @Inject constructor(
    private val digitalInk: DigitalInk
): ViewModel() {

    data class State(
        val resetCanvas: Boolean = false,
        val showModelStatusProgress: Boolean = false,
        val modelStatusText: String = "",
        val finalText: String = "",
        val predictions: List<String> = emptyList(),
    )

    sealed class Event {
        data class Pointer(val event: DrawEvent): Event()
    }

    private var finishRecordingJob: Job? = null

    private val _modelStatus = MutableStateFlow<ModelStatusUi>(ModelStatusUi.None)
    private val _predictions = MutableStateFlow<List<String>>(emptyList())
    private val _resetCanvas = MutableStateFlow<Boolean>(false)
    private val _finalText = MutableStateFlow<String>("")

    val state: StateFlow<State>
        get() = combine(
            _modelStatus,
            _resetCanvas,
            _predictions,
            _finalText,
        ) { modelStatus, resetCanvas, predictions, finalText ->
            State(
                resetCanvas = resetCanvas,
                showModelStatusProgress = modelStatus != ModelStatusUi.Ready,
                modelStatusText = when (modelStatus) {
                    ModelStatusUi.CheckingDownload -> "Checking model..."
                    ModelStatusUi.Downloading -> "Downloading model..."
                    else -> ""
                },
                finalText = finalText,
                predictions = predictions
            )
        }.stateIn(viewModelScope, SharingStarted.Lazily, State())

    init {
        digitalInk.checkIfModelIsDownloaded {
            when (it) {
                MLKitModelStatus.Downloading -> _modelStatus.value = ModelStatusUi.Downloading
                MLKitModelStatus.Downloaded -> _modelStatus.value = ModelStatusUi.Ready
                MLKitModelStatus.CheckingDownload -> _modelStatus.value = ModelStatusUi.CheckingDownload
            }
        }
    }

    fun onEvent(event: Event) {

        when (val drawEvent = (event as? Event.Pointer)?.event) {
            is DrawEvent.Down -> {
                this.finishRecordingJob?.cancel()
                _resetCanvas.value = false

                digitalInk.record(drawEvent.x, drawEvent.y)
            }

            is DrawEvent.Move -> {
                digitalInk.record(drawEvent.x, drawEvent.y)
            }

            is DrawEvent.Up -> {
                this.finishRecordingJob = viewModelScope.launch {
                    delay(DEBOUNCE_INTERVAL)
                    digitalInk.finishRecording { predictions ->
                        _resetCanvas.value = true
                        _predictions.value = predictions
                        _finalText.value = _finalText.value.plus(predictions[0])
                    }
                }
            }
        }
    }

    fun onPredictionSelected(prediction: String) {
        _predictions.value = emptyList()
        _finalText.value = _finalText.value.dropLast(1).plus(prediction)
    }

    companion object {
        private const val DEBOUNCE_INTERVAL = 1000L
    }
}

enum class ModelStatusUi {
    CheckingDownload, Downloading, Ready, None
}