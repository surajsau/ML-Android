package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.data.FaceDetectionProvider
import `in`.surajsau.jisho.data.FacesDataProvider
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.data.db.FaceImage
import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.face.Face
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FacenetViewModelImpl @Inject constructor(
    private val fileProvider: FileProvider,
    private val facesDataProvider: FacesDataProvider,
    private val faceDetectionProvider: FaceDetectionProvider
) : ViewModel(), FacenetViewModel {

    init {
        viewModelScope.launch {
            facesDataProvider.getAllImages()
                .flowOn(Dispatchers.IO)
                .collect {
                    _images.value = it

                    _screenMode.value = if (it.isEmpty())
                        FacenetViewModel.ScreenMode.Empty
                    else
                        FacenetViewModel.ScreenMode.Gallery
                }

            facesDataProvider.getFaces()
                .flowOn(Dispatchers.IO)
                .collect { _peopleImages.value = it }
        }
    }

    private val _images = MutableStateFlow(emptyList<FaceImage>())

    private val _peopleImages = MutableStateFlow(emptyList<FaceImage>())

    private val _screenMode = MutableStateFlow(FacenetViewModel.ScreenMode.Empty)

    private val _imageDialogMode = MutableStateFlow<FacenetViewModel.ImageDialogMode>(FacenetViewModel.ImageDialogMode.DontShow)

    override val state: StateFlow<FacenetViewModel.State>
        get() = combine(
            _peopleImages,
            _images,
            _screenMode,
            _imageDialogMode
        ) { peopleImages, images, screenMode, imageDialogMode ->
            FacenetViewModel.State(
                personImages = peopleImages,
                images = images,
                screenMode = screenMode,
                imageDialogMode = imageDialogMode
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, FacenetViewModel.State())

    override fun onEvent(event: FacenetViewModel.Event) {
        when (event) {
            is FacenetViewModel.Event.AddNewFaceClicked -> _screenMode.value = FacenetViewModel.ScreenMode.AddFace
            is FacenetViewModel.Event.ClassifyFaceClicked -> _screenMode.value = FacenetViewModel.ScreenMode.RecogniseFace
            is FacenetViewModel.Event.DismissImageDialog-> _imageDialogMode.value = FacenetViewModel.ImageDialogMode.DontShow
            is FacenetViewModel.Event.CameraResultReceived -> {
                    viewModelScope.launch {
                        fileProvider.fetchCachedBitmap(fileName = event.fileName)
                            .flatMapLatest { faceDetectionProvider.getFaces(it) }
                            .flowOn(Dispatchers.IO)
                            .collect { faces ->
                                val filePath = fileProvider.getCacheFilePath(fileName = event.fileName)

                                Log.e("Facenet", "cache file path: $filePath")
                                Log.e("Facenet", "face: ${faces[0].boundingBox.top}-${faces[0].boundingBox.left}")

                                _imageDialogMode.value = when (_screenMode.value) {
                                    FacenetViewModel.ScreenMode.AddFace -> {
                                        FacenetViewModel.ImageDialogMode.ShowAddFace(
                                            filePath = filePath,
                                            fileName = event.fileName,
                                            face = faces[0]
                                        )
                                    }

                                    FacenetViewModel.ScreenMode.RecogniseFace -> {
                                        FacenetViewModel.ImageDialogMode.ShowRecogniseFace(
                                            filePath = filePath,
                                            isLoading = true
                                        )
                                    }

                                    else -> FacenetViewModel.ImageDialogMode.DontShow
                                }
                            }
                    }
            }

            is FacenetViewModel.Event.FaceNameReceived -> {
                viewModelScope.launch {
                    fileProvider.fetchCachedBitmap(fileName = event.fileName)
                        .onEach {
                            fileProvider.storeBitmap(
                                folderName = "images/faces",
                                fileName = event.fileName,
                                bitmap = it
                            )
                        }
                        .flowOn(Dispatchers.IO)
                        .collect { facesDataProvider.saveNewFace(faceName = event.faceName, fileName = event.fileName) }
                    _imageDialogMode.value = FacenetViewModel.ImageDialogMode.DontShow
                }
            }

            is FacenetViewModel.Event.FaceSelected -> {
                viewModelScope.launch {
                    facesDataProvider.getImagesForFace(faceName = event.faceName)
                        .flowOn(Dispatchers.IO)
                        .collect { _images.value = it }
                }
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
        object DismissImageDialog: Event()
        data class FaceNameReceived(val fileName: String, val faceName: String): Event()
        data class FaceSelected(val faceName: String): Event()
        data class CameraResultReceived(val fileName: String): Event()
    }

    data class State(
        val personImages: List<FaceImage> = emptyList(),
        val images: List<FaceImage> = emptyList(),
        val screenMode: ScreenMode = ScreenMode.Empty,
        val imageDialogMode: ImageDialogMode = ImageDialogMode.DontShow
    )

    enum class ScreenMode {
        Gallery, AddFace, RecogniseFace, Empty
    }

    sealed class ImageDialogMode {
        object DontShow: ImageDialogMode()
        data class ShowAddFace(val filePath: String, val fileName: String, val face: Face): ImageDialogMode()
        data class ShowRecogniseFace(val isLoading: Boolean, val filePath: String): ImageDialogMode()
    }
}

val LocalFacenetViewModel = compositionLocalOf<FacenetViewModel> { error("FacenetViewModel factory not provided") }