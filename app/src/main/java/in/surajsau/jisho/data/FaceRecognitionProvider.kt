package `in`.surajsau.jisho.data

import javax.inject.Inject

class FaceRecognitionProviderImpl @Inject constructor(): FaceRecognitionProvider {

    override fun recognizeFace() {

    }

}

interface FaceRecognitionProvider {

    fun recognizeFace()
}