package `in`.surajsau.jisho.domain.facenet

import `in`.surajsau.jisho.data.FacesDataProvider
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.domain.models.GalleryImageModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchAllImagesForFace @Inject constructor(
    private val facesDataProvider: FacesDataProvider,
    private val fileProvider: FileProvider,
) {

    fun invoke(faceName: String): Flow<List<GalleryImageModel>> = facesDataProvider
        .getImagesForFace(faceName = faceName)
        .map { images ->
            images.map {
                GalleryImageModel(
                    imageFilePath = fileProvider.getFilePath(
                        folderName = FileProvider.FACENET_IMAGE_FOLDER,
                        fileName = it.fileName
                    ),
                    faceName = it.faceName
                )
            }
        }
}