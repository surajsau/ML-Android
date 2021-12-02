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
    private val digitalInk: DigitalInk,
    private val translator: Translator,
): ViewModel() {

    data class State(
        val resetCanvas: Boolean = false,
        val showModelStatusProgress: Boolean = false,
        val finalText: String = "",
        val translation: String = "",
        val predictions: List<String> = emptyList(),
    )

    sealed class Event {
        data class Pointer(val event: DrawEvent): Event()
    }

    private var finishRecordingJob: Job? = null

    private val _digitalInkModelStatus = digitalInk.checkIfModelIsDownloaded()
        .flatMapLatest {
            if (it == MLKitModelStatus.Downloaded)
                flowOf(it)
            else
                digitalInk.downloadModel()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, MLKitModelStatus.NotDownloaded)

    private val _translatorModelStatus = translator.checkIfModelIsDownloaded()
        .stateIn(viewModelScope, SharingStarted.Lazily, MLKitModelStatus.NotDownloaded)

    private val _predictions = digitalInk.predictions
        .consumeAsFlow()
        .onEach {
            if (it.isEmpty())
                return@onEach

            setFinalText(text = _finalText.value.plus(it[0]))
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _translation = translator.translation
        .consumeAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    private val _finalText = MutableStateFlow<String>("")
    private val _resetCanvas = MutableStateFlow<Boolean>(false)

    val state: StateFlow<State>
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

            State(
                resetCanvas = resetCanvas,
                showModelStatusProgress = !areModelsDownloaded,
                finalText = finalText,
                translation = translation,
                predictions = predictions
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, State())

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
                    _resetCanvas.value = true
                    digitalInk.finishRecording()
                }
            }
        }
    }

    fun onPredictionSelected(prediction: String) {
        setFinalText(text = _finalText.value.dropLast(1).plus(prediction))
    }

    fun onTextChanged(text: String) {
        setFinalText(text)
    }

    private fun setFinalText(text: String) {
        _finalText.value = text

        if (text.isNotEmpty())
            translator.translate(text)
    }

    fun onStop() {
        digitalInk.close()
        translator.close()
    }

    companion object {
        private const val DEBOUNCE_INTERVAL = 1000L
    }
}

enum class ModelStatusUi {
    CheckingDownload, Downloading, Ready, None
}