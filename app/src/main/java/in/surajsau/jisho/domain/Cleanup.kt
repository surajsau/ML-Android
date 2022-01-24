package `in`.surajsau.jisho.domain

import `in`.surajsau.jisho.data.CardDataProvider
import `in`.surajsau.jisho.data.facenet.FaceRecognitionProvider
import javax.inject.Inject

class Cleanup @Inject constructor(
    private val faceRecognitionProvider: FaceRecognitionProvider,
) {
    fun invoke() {
        faceRecognitionProvider.close()
    }
}