package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.domain.Cleanup
import `in`.surajsau.jisho.domain.facenet.*
import `in`.surajsau.jisho.domain.models.FaceModel
import `in`.surajsau.jisho.domain.models.FaceRecognitionResult
import `in`.surajsau.jisho.domain.models.GalleryModel
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
    private val initiate: Initiate,
    private val cleanup: Cleanup,
    private val loadEmbeddings: LoadEmbeddings,
    private val detectFaces: DetectFaces,
    private val saveFaceEmbedding: SaveFaceEmbedding,
    private val saveImage: SaveImage,
    private val saveNewFace: SaveNewFace,
    private val fetchAllImages: FetchAllImages,
    private val fetchAllFaces: FetchAllFaces,
    private val fetchAllImagesForFace: FetchAllImagesForFace,
    private val fetchFaceNames: FetchFaceNames,
) : ViewModel(), FacenetViewModel {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadEmbeddings.invoke()
            refreshData()
        }
    }

    private val _images = MutableStateFlow(emptyList<GalleryModel>())

    private val _peopleImages = MutableStateFlow(emptyList<FaceModel>())

    private val _screenMode = MutableStateFlow(FacenetViewModel.ScreenMode.Empty)

    private val _checkFaceDialog = MutableStateFlow<FacenetViewModel.CheckFaceDialog>(FacenetViewModel.CheckFaceDialog.DontShow)

    private val _showLoader = MutableStateFlow(false)

    private val _filterMode = MutableStateFlow<FacenetViewModel.FilterMode>(FacenetViewModel.FilterMode.None)

    override val state: StateFlow<FacenetViewModel.State>
        get() = combine(
            _peopleImages,
            _images,
            _screenMode,
            _checkFaceDialog,
            _showLoader,
            _filterMode,
        ) { result ->
            Log.e("Gallery", "${result[5]}")
            FacenetViewModel.State(
                personImages = result[0] as List<FaceModel>,
                images = (result[1] as List<GalleryModel>).let {
                    // add empty cells at the end of list
                    val emptyCellsRequired = it.size % FacenetViewModel.GalleryModelsPerRow
                    it.toMutableList().apply {
                        repeat(emptyCellsRequired) { add(GalleryModel.Empty) }
                    }
                },
                screenMode = result[2] as FacenetViewModel.ScreenMode,
                checkFaceDialog = result[3] as FacenetViewModel.CheckFaceDialog,
                showLoader = result[4] as Boolean,
                filterMode = result[5] as FacenetViewModel.FilterMode
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, FacenetViewModel.State())

    // util get for quick access of current face recognition results
    private val _faceResults: List<FaceRecognitionResult>
        get() = (_checkFaceDialog.value as? FacenetViewModel.CheckFaceDialog.Show)?.recognitionResults ?: emptyList()

    override fun onEvent(event: FacenetViewModel.Event) {
        Log.e("Facenet", "$event")

        when (event) {
            is FacenetViewModel.Event.Initiate -> initiate.invoke()
            is FacenetViewModel.Event.Close -> cleanup.invoke()
            is FacenetViewModel.Event.OpenCameraClicked -> _screenMode.value = FacenetViewModel.ScreenMode.AddFace
            is FacenetViewModel.Event.DismissCheckFaceDialog-> {
                _checkFaceDialog.value = FacenetViewModel.CheckFaceDialog.DontShow
                _screenMode.value = FacenetViewModel.ScreenMode.Gallery

                viewModelScope.launch { refreshData() }
            }
            is FacenetViewModel.Event.CameraResultReceived -> {
                viewModelScope.launch {
                    detectFaces.invoke(fileName = event.fileName)
                        .zip(fetchFaceNames.invoke()) { result, names -> Pair(result, names) }
                        .onStart { _showLoader.value = true }
                        .flowOn(Dispatchers.IO)
                        .collect { (results, faceNames) ->
                            _showLoader.value = false

                            if (results.isEmpty()) {
                                _checkFaceDialog.value = FacenetViewModel.CheckFaceDialog.DontShow
                                return@collect
                            }
                            _checkFaceDialog.value = FacenetViewModel.CheckFaceDialog.Show(
                                recognitionResults = results,
                                imageFileName = event.fileName,
                                faceNameSuggestions = faceNames,
                            )
                        }
                }
            }

            is FacenetViewModel.Event.FaceConfirmed -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (event.isNewFace) {
                        saveFaceEmbedding.invoke(faceName = event.faceName, faceFileName = event.faceFileName)
                        saveNewFace.invoke(faceName = event.faceName, fileName = event.faceFileName)
                    }

                    saveImage.invoke(faceName = event.faceName, fileName = event.imageFileName)
                }
            }

            is FacenetViewModel.Event.FaceNotConfirmed -> {
                viewModelScope.launch(Dispatchers.IO) {
                    saveImage.invoke(faceName = "", fileName = event.imageFileName)
                }
            }

            is FacenetViewModel.Event.FaceSelected -> {
                viewModelScope.launch {
                    val previousSelectedFace = (_filterMode.value as? FacenetViewModel.FilterMode.FaceSelected)?.faceName ?: ""

                    _filterMode.value = if (previousSelectedFace != event.faceName) {
                        FacenetViewModel.FilterMode.FaceSelected(event.faceName)
                    } else {
                        FacenetViewModel.FilterMode.None
                    }

                    val imagesFlow = if (previousSelectedFace != event.faceName) {
                        fetchAllImagesForFace.invoke(faceName = event.faceName)
                    } else {
                        fetchAllImages.invoke()
                    }

                    imagesFlow
                        .flowOn(Dispatchers.IO)
                        .collect { _images.value = it }
                }
            }
        }
    }

    private suspend fun refreshData() {
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

interface FacenetViewModel: SingleFlowViewModel<FacenetViewModel.Event, FacenetViewModel.State> {

    sealed class Event {
        object OpenCameraClicked: Event()
        object DismissCheckFaceDialog: Event()

        data class FaceConfirmed(
            val imageFileName: String,
            val faceFileName: String,
            val faceName: String,
            val isNewFace: Boolean
        ): Event()

        data class FaceNotConfirmed(
            val imageFileName: String,
            val faceFileName: String
        ): Event()

        data class FaceSelected(val faceName: String): Event()
        data class CameraResultReceived(val fileName: String): Event()

        object Close: Event()
        object Initiate: Event()
    }

    data class State(
        val personImages: List<FaceModel> = emptyList(),
        val images: List<GalleryModel> = emptyList(),
        val screenMode: ScreenMode = ScreenMode.Empty,
        val checkFaceDialog: CheckFaceDialog = CheckFaceDialog.DontShow,
        val showLoader: Boolean = false,
        val filterMode: FilterMode = FilterMode.None
    )

    enum class ScreenMode {
        Gallery, AddFace, RecogniseFace, Empty
    }

    sealed class CheckFaceDialog {
        object DontShow: CheckFaceDialog()

        data class Show(
            val faceNameSuggestions: List<String>,
            val recognitionResults: List<FaceRecognitionResult>,
            val imageFileName: String,
        ): CheckFaceDialog()
    }

    companion object {
        const val GalleryModelsPerRow = 3
    }

    sealed class FilterMode {
        data class FaceSelected(val faceName: String): FilterMode()
        object None: FilterMode()
    }
}

val LocalFacenetViewModel = compositionLocalOf<FacenetViewModel> { error("FacenetViewModel factory not provided") }