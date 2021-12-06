package `in`.surajsau.jisho.ui.styletransfer

import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.data.StyleCacheStatus
import `in`.surajsau.jisho.data.StyleTransferProvider
import android.graphics.Bitmap
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StyleTransferViewModelImpl @Inject constructor(
    private val styleTransferProvider: StyleTransferProvider,
) : ViewModel(), StyleTransferViewModel {

    private val _cameraImage = MutableStateFlow<String>("")
    private val _styleImage = MutableStateFlow<String>("")

    private val _cacheStatus = MutableStateFlow<StyleCacheStatus>(StyleCacheStatus.Checking)

    private val _isImageCaptured: Flow<Boolean>
        get() = _cameraImage.map { it.isNotEmpty() }

    private val _styleTransferredImage = combine(
        _cameraImage.filter { it.isNotEmpty() },
        _styleImage.filter { it.isNotEmpty() },
        transform = { cameraImage, styleImage -> Pair(cameraImage, styleImage) }
    ).flatMapLatest { (targetPath, stylePath) ->
        styleTransferProvider.process(targetPath, stylePath)
    }

    override val state: StateFlow<StyleTransferViewModel.State>
        get() = combine(
            _cacheStatus,
            _styleTransferredImage,
            _isImageCaptured
        ){ cacheStatus, outputImage, isImagedCaptured ->
            val mode = if (!isImagedCaptured) {
                StyleTransferViewModel.ScreenMode.Camera
            } else {
                StyleTransferViewModel.ScreenMode.StylePreview(
                    showLoading = cacheStatus == StyleCacheStatus.Checking,
                    image = outputImage,
                    stylePreviews = StyleTransferProvider.Styles
                )
            }

            StyleTransferViewModel.State(mode = mode)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, StyleTransferViewModel.State())

    override fun onEvent(event: StyleTransferViewModel.Event) {
        when(event) {
            is StyleTransferViewModel.Event.CameraResultReceived -> {
                styleTransferProvider.checkStylesCacheStatus()
                    .onEach { _cacheStatus.value = it }
                    .launchIn(viewModelScope)
                _cameraImage.value = event.fileName
            }
            is StyleTransferViewModel.Event.StyleSelected -> _styleImage.value = event.fileName

            is StyleTransferViewModel.Event.OnStop -> styleTransferProvider.close()
        }
    }

}

interface StyleTransferViewModel: SingleFlowViewModel<StyleTransferViewModel.Event, StyleTransferViewModel.State> {

    sealed class ScreenMode {
        object Camera: ScreenMode()

        data class StylePreview(
            val showLoading: Boolean,
            val image: Bitmap,
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