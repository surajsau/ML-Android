package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.base.FileName
import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.data.FaceDetectionProvider
import `in`.surajsau.jisho.data.FacesDataProvider
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.data.db.Face
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class FacenetViewModelImpl constructor(
    private val facesDataProvider: FacesDataProvider,
    private val fileProvider: FileProvider,
    private val faceDetectionProvider: FaceDetectionProvider
) : ViewModel(), FacenetViewModel {

    init {

    }

    private val _peopleImages = MutableStateFlow(emptyList<Face>())

    private val _screenMode = MutableStateFlow(FacenetViewModel.ScreenMode.Empty)

    override val state: StateFlow<FacenetViewModel.State>
        get() = TODO("Not yet implemented")

    override fun onEvent(event: FacenetViewModel.Event) {
        when (event) {
            is FacenetViewModel.Event.AddNewFaceClicked -> _screenMode.value = FacenetViewModel.ScreenMode.AddFace
            is FacenetViewModel.Event.ClassifyFaceClicked -> _screenMode.value = FacenetViewModel.ScreenMode.RecogniseFace
            is FacenetViewModel.Event.CameraResultReceived -> {

            }

            is FacenetViewModel.Event.Refresh -> {
                viewModelScope.launch {
                    facesDataProvider.getFaces()
                        .flowOn(Dispatchers.IO)
                        .collect { _peopleImages.value = it }
                }
            }
        }
    }

}

interface FacenetViewModel: SingleFlowViewModel<FacenetViewModel.Event, FacenetViewModel.State> {

    sealed class Event {
        object Refresh: Event()
        object AddNewFaceClicked: Event()
        object ClassifyFaceClicked: Event()
        object CameraPermissionDenied: Event()
        data class FaceSelected(val faceName: String): Event()
        data class CameraResultReceived(val fileName: String): Event()
    }

    data class State(
        val personImages: List<Face> = emptyList(),
        val images: List<String> = emptyList(),
        val screenMode: ScreenMode = ScreenMode.Empty
    )

    enum class ScreenMode {
        Gallery, AddFace, RecogniseFace, Empty
    }
}

val LocalFacenetViewModel = compositionLocalOf<FacenetViewModel> { error("FacenetViewModel factory not provided") }