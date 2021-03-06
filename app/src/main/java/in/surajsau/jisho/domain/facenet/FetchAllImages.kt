package `in`.surajsau.jisho.domain.facenet

import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.data.facenet.FacesDataProvider
import `in`.surajsau.jisho.domain.models.GalleryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchAllImages @Inject constructor(
    private val facesDataProvider: FacesDataProvider,
    private val fileProvider: FileProvider,
) {

    fun invoke(): Flow<List<GalleryModel>> = facesDataProvider.getAllImages(isPrimary = false)
        .map { images ->
            images.map {
                GalleryModel.Image(
                    imageFilePath = fileProvider.getFilePath(FileProvider.FACENET_IMAGE_FOLDER, it.fileName),
                    faceName = it.faceName
                )
            }
        }
}