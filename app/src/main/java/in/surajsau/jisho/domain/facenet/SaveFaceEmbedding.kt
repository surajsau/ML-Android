package `in`.surajsau.jisho.domain.facenet

import `in`.surajsau.jisho.data.FaceRecognitionProvider
import `in`.surajsau.jisho.data.FileProvider
import javax.inject.Inject

class SaveFaceEmbedding @Inject constructor(
    private val faceRecognitionProvider: FaceRecognitionProvider,
    private val fileProvider: FileProvider,
) {

    suspend fun invoke(faceName: String, faceFileName: String) {
        val bitmap = fileProvider.fetchCachedBitmap(fileName = faceFileName)
        val embeddings = faceRecognitionProvider.generateEmbedding(bitmap)
        fileProvider.saveEmbeddings(folderName = FileProvider.FACENET_EMBEDDINGS_FOLDER, fileName = faceName, embedding = embeddings)
    }
}