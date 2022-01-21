package `in`.surajsau.jisho.domain.facenet

import `in`.surajsau.jisho.data.FacesDataProvider
import `in`.surajsau.jisho.data.FileProvider
import javax.inject.Inject

class SaveNewFace @Inject constructor(
    private val fileProvider: FileProvider,
    private val facesDataProvider: FacesDataProvider,
) {

    suspend fun invoke(faceName: String, fileName: String) {
        val bitmap = fileProvider.fetchCachedBitmap(fileName = fileName)
        fileProvider.storeBitmap(folderName = FileProvider.FACENET_IMAGE_FOLDER, fileName = fileName, bitmap = bitmap)

        facesDataProvider.saveFace(faceName = faceName, fileName = fileName, isPrimary = true)
    }
}