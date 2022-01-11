package `in`.surajsau.jisho.domain.facenet

import `in`.surajsau.jisho.data.FaceRecognitionProvider
import `in`.surajsau.jisho.data.FileProvider
import javax.inject.Inject

class SaveFaceEmbedding @Inject constructor(
    private val fileProvider: FileProvider,
    private val faceRecognitionProvider: FaceRecognitionProvider,
) {

    suspend fun invoke(faceName: String, faceFileName: String) {
        val bitmap = fileProvider.fetchCachedBitmap(fileName = faceFileName)
        val embeddings = faceRecognitionProvider.generateEmbedding(bitmap)
        faceRecognitionProvider.saveEmbedding(faceName = faceName, embedding = embeddings)
    }

}