package `in`.surajsau.jisho.domain.facenet

import `in`.surajsau.jisho.data.FaceRecognitionProvider
import `in`.surajsau.jisho.data.FileProvider
import javax.inject.Inject

class RecogniseFace @Inject constructor(
    private val fileProvider: FileProvider,
    private val faceRecognitionProvider: FaceRecognitionProvider,
) {

    suspend fun invoke(fileName: String): String {
        val bitmap = fileProvider.fetchCachedBitmap(fileName = fileName)
        val embeddings = faceRecognitionProvider.generateEmbedding(bitmap = bitmap)

        val similarities = faceRecognitionProvider.compareEmbedding(embedding = embeddings)
        val greatestSimilarity = similarities.entries.maxByOrNull { it.value } ?: return ""

        return if (greatestSimilarity.value > 0.4) greatestSimilarity.key else ""
    }
}