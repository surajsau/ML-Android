package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.data.FaceDetectionProvider
import `in`.surajsau.jisho.data.FileProvider
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class FacenetViewModelImpl constructor(
    private val fileProvider: FileProvider,
    private val faceDetectionProvider: FaceDetectionProvider
) : ViewModel(), FacenetViewModel {

    init {

    }

    override val state: StateFlow<FacenetViewModel.State>
        get() = TODO("Not yet implemented")

    override fun onEvent(event: FacenetViewModel.Event) {
        when (event) {

        }
    }

}

interface FacenetViewModel: SingleFlowViewModel<FacenetViewModel.Event, FacenetViewModel.State> {

    sealed class Event {
        object AddNewFaceClicked: Event()
        object ClassifyFaceClicked: Event()
        data class CameraResultReceived(val imageProxy: ImageProxy): Event()
    }

    data class State(val error: Exception)
}