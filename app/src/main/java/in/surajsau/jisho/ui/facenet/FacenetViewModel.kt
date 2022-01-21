package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.data.db.FaceImage
import `in`.surajsau.jisho.domain.facenet.*
import `in`.surajsau.jisho.domain.models.FaceModel
import `in`.surajsau.jisho.domain.models.GalleryImageModel
import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class FacenetViewModelImpl @Inject constructor(
    private val fileProvider: FileProvider,
    private val detectFaces: DetectFaces,
    private val saveFaceEmbedding: SaveFaceEmbedding,
    private val saveImage: SaveImage,
    private val saveNewFace: SaveNewFace,
    private val fetchAllImages: FetchAllImages,
    private val fetchAllFaces: FetchAllFaces,
    private val fetchAllImagesForFace: FetchAllImagesForFace,
) : ViewModel(), FacenetViewModel {

    init {
        viewModelScope.launch {
            fetchAllImages.invoke()
                .flowOn(Dispatchers.IO)
                .collect {
                    _images.value = it

                    _screenMode.value = if (it.isEmpty())
                        FacenetViewModel.ScreenMode.Empty
                    else
                        FacenetViewModel.ScreenMode.Gallery
                }

            fetchAllFaces.invoke()
                .flowOn(Dispatchers.IO)
                .collect { _peopleImages.value = it }
        }
    }

    private val _images = MutableStateFlow(emptyList<GalleryImageModel>())

    private val _peopleImages = MutableStateFlow(emptyList<FaceModel>())

    private val _screenMode = MutableStateFlow(FacenetViewModel.ScreenMode.Empty)

    private val _imageDialogMode = MutableStateFlow<FacenetViewModel.ImageDialogMode>(FacenetViewModel.ImageDialogMode.DontShow)

    private val _showLoader = MutableStateFlow(false)

    override val state: StateFlow<FacenetViewModel.State>
        get() = combine(
            _peopleImages,
            _images,
            _screenMode,
            _imageDialogMode,
            _showLoader,
        ) { peopleImages, images, screenMode, imageDialogMode, showLoader ->
            FacenetViewModel.State(
                personImages = peopleImages,
                images = images,
                screenMode = screenMode,
                imageDialogMode = imageDialogMode,
                showLoader = showLoader
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, FacenetViewModel.State())

    override fun onEvent(event: FacenetViewModel.Event) {
        Log.e("Facenet", "$event")

        when (event) {
            is FacenetViewModel.Event.AddNewFaceClicked -> _screenMode.value = FacenetViewModel.ScreenMode.AddFace
            is FacenetViewModel.Event.ClassifyFaceClicked -> _screenMode.value = FacenetViewModel.ScreenMode.RecogniseFace
            is FacenetViewModel.Event.DismissImageDialog-> _imageDialogMode.value = FacenetViewModel.ImageDialogMode.DontShow
            is FacenetViewModel.Event.CameraResultReceived -> {
                viewModelScope.launch {
                    detectFaces.invoke(fileName = event.fileName)
                        .onStart { _showLoader.value = true }
                        .flowOn(Dispatchers.IO)
                        .collect { faceFileNames ->
                            _showLoader.value = false

                            if (faceFileNames.isEmpty()) {
                                _imageDialogMode.value = FacenetViewModel.ImageDialogMode.DontShow
                                return@collect
                            }

                            val filePath = fileProvider.getCacheFilePath(fileName = faceFileNames[0])

                            _imageDialogMode.value = when (_screenMode.value) {
                                FacenetViewModel.ScreenMode.AddFace -> {
                                    FacenetViewModel.ImageDialogMode.ShowAddFace(
                                        faceFilePath = filePath,
                                        faceFileName = faceFileNames[0],
                                        imageFileName = event.fileName,
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
                viewModelScope.launch(Dispatchers.IO) {
                    saveFaceEmbedding.invoke(faceName = event.faceName, faceFileName = event.faceFileName)
                    saveNewFace.invoke(faceName = event.faceName, fileName = event.faceFileName)
                    saveImage.invoke(faceName = event.faceName, fileName = event.imageFileName)

                    _imageDialogMode.value = FacenetViewModel.ImageDialogMode.DontShow

                    _screenMode.value = FacenetViewModel.ScreenMode.Gallery
                }

                refreshImages()
            }

            is FacenetViewModel.Event.FaceSelected -> {
                viewModelScope.launch {
                    fetchAllImagesForFace.invoke(faceName = event.faceName)
                        .flowOn(Dispatchers.IO)
                        .collect { _images.value = it }
                }
            }
        }
    }

    private fun refreshImages() {
        viewModelScope.launch {
            fetchAllFaces.invoke()
                .flowOn(Dispatchers.IO)
                .collect { _peopleImages.value = it }
        }
    }

}

interface FacenetViewModel: SingleFlowViewModel<FacenetViewModel.Event, FacenetViewModel.State> {

    sealed class Event {
        object AddNewFaceClicked: Event()
        object ClassifyFaceClicked: Event()
        object CameraPermissionDenied: Event()
        object DismissImageDialog: Event()
        data class FaceNameReceived(val imageFileName: String, val faceFileName: String, val faceName: String): Event()
        data class FaceSelected(val faceName: String): Event()
        data class CameraResultReceived(val fileName: String): Event()
    }

    data class State(
        val personImages: List<FaceModel> = emptyList(),
        val images: List<GalleryImageModel> = emptyList(),
        val screenMode: ScreenMode = ScreenMode.Empty,
        val imageDialogMode: ImageDialogMode = ImageDialogMode.DontShow,
        val showLoader: Boolean = false,
    )

    enum class ScreenMode {
        Gallery, AddFace, RecogniseFace, Empty
    }

    sealed class ImageDialogMode {
        object DontShow: ImageDialogMode()

        data class ShowAddFace(
            val faceFilePath: String,
            val imageFileName: String,
            val faceFileName: String
        ): ImageDialogMode()

        data class ShowRecogniseFace(val isLoading: Boolean, val filePath: String): ImageDialogMode()
    }
}

val LocalFacenetViewModel = compositionLocalOf<FacenetViewModel> { error("FacenetViewModel factory not provided") }