package `in`.surajsau.jisho.domain.facenet

import `in`.surajsau.jisho.data.FacesDataProvider
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.data.db.FaceImage
import `in`.surajsau.jisho.domain.models.FaceModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchAllFaces @Inject constructor(
    private val facesDataProvider: FacesDataProvider,
    private val fileProvider: FileProvider,
) {

    fun invoke(): Flow<List<FaceModel>> = facesDataProvider.getAllImages(isPrimary = true)
        .map { images ->
            images.map {
                FaceModel(
                    imageFilePath = fileProvider.getFilePath(folderName = FileProvider.FACENET_IMAGE_FOLDER, fileName = it.fileName),
                    faceName = it.faceName
                )
            }
        }
}