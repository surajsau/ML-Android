package `in`.surajsau.jisho.domain.facenet

import `in`.surajsau.jisho.data.FaceRecognitionProvider
import `in`.surajsau.jisho.data.FacesDataProvider
import javax.inject.Inject

class LoadEmbeddings @Inject constructor(
    private val faceRecognitionProvider: FaceRecognitionProvider,
    private val facesDataProvider: FacesDataProvider,
) {

    suspend fun invoke() {
        val faceNames = facesDataProvider.getFaceNames()
        faceNames.forEach { faceRecognitionProvider.loadEmbeddingFor(faceName = it) }
    }
}