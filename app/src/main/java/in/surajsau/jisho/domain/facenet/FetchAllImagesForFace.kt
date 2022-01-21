package `in`.surajsau.jisho.domain.facenet

import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.data.facenet.FacesDataProvider
import `in`.surajsau.jisho.domain.models.GalleryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchAllImagesForFace @Inject constructor(
    private val facesDataProvider: FacesDataProvider,
    private val fileProvider: FileProvider,
) {

    fun invoke(faceName: String): Flow<List<GalleryModel>> = facesDataProvider
        .getImagesForFace(faceName = faceName)
        .map { images ->
            images.map {
                GalleryModel.Image(
                    imageFilePath = fileProvider.getFilePath(
                        folderName = FileProvider.FACENET_IMAGE_FOLDER,
                        fileName = it.fileName
                    ),
                    faceName = it.faceName
                )
            }
        }
}