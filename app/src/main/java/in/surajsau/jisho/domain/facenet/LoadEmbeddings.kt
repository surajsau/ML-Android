package `in`.surajsau.jisho.domain.facenet

import `in`.surajsau.jisho.data.facenet.FaceRecognitionProvider
import `in`.surajsau.jisho.data.facenet.FacesDataProvider
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