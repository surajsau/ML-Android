package `in`.surajsau.jisho.ui.styletransfer

import `in`.surajsau.jisho.base.FileName
import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.data.StyleTransferProvider
import android.graphics.Bitmap
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StyleTransferViewModelImpl @Inject constructor(
    private val styleTransferProvider: StyleTransferProvider,
    private val fileProvider: FileProvider,
) : ViewModel(), StyleTransferViewModel {

    private val _cameraImageFileName = MutableStateFlow<FileName?>(null)
    private val _styleImageFileName = MutableStateFlow<FileName?>(null)

    private val _isImageCaptured = MutableStateFlow(false)

    private val _cameraImage = _cameraImageFileName.filterNotNull().flatMapLatest { fileProvider.fetchBitmap(it.value) }
    private val _styleImage = _styleImageFileName.filterNotNull().flatMapLatest { fileProvider.fetchAssetBitmap(it.value) }

    private val _styleTransferState = combine(
        _cameraImage,
        _styleImage
    ) { cameraIamge, styleImage -> Pair(cameraIamge, styleImage) }
        .flatMapLatest { (targetImage, styleImage) -> styleTransferProvider.process(targetImage, styleImage) }
        .stateIn(viewModelScope, SharingStarted.Lazily, StyleTransferProvider.StyleTransferState.Idle)

    override val state: StateFlow<StyleTransferViewModel.State>
        get() = combine(
            _styleTransferState,
            _isImageCaptured
        ){ styleTransferState, isImagedCaptured ->
            val mode = if (!isImagedCaptured) {
                StyleTransferViewModel.ScreenMode.Camera
            } else {
                StyleTransferViewModel.ScreenMode.StylePreview(
                    showLoading = styleTransferState !is StyleTransferProvider.StyleTransferState.Finished,
                    image = (styleTransferState as? StyleTransferProvider.StyleTransferState.Finished)?.image,
                    stylePreviews = StyleTransferProvider.Styles
                )
            }

            StyleTransferViewModel.State(mode = mode)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, StyleTransferViewModel.State())

    override fun onEvent(event: StyleTransferViewModel.Event) {
        viewModelScope.launch {
            when (event) {
                is StyleTransferViewModel.Event.CameraResultReceived -> {
                    _isImageCaptured.value = true
                    _cameraImageFileName.value = FileName(event.fileName)
                }

                is StyleTransferViewModel.Event.StyleSelected -> { _styleImageFileName.value = FileName(event.fileName) }

                is StyleTransferViewModel.Event.OnStop -> {}
            }
        }
    }

}

interface StyleTransferViewModel: SingleFlowViewModel<StyleTransferViewModel.Event, StyleTransferViewModel.State> {

    sealed class ScreenMode {
        object Camera: ScreenMode()

        data class StylePreview(
            val showLoading: Boolean,
            val image: Bitmap?,
            val stylePreviews: List<String>
        ): ScreenMode()
    }

    sealed class Event {
        data class CameraResultReceived(val fileName: String): Event()
        data class StyleSelected(val fileName: String): Event()

        object OnStop: Event()
    }

    data class State(
        val mode: ScreenMode = ScreenMode.Camera
    )
}

val LocalStyleTransferViewModel = compositionLocalOf<StyleTransferViewModel> {
    error("StyleTransferViewModel not provided")
}