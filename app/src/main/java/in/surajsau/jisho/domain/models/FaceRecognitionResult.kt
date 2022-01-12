package `in`.surajsau.jisho.domain.models

sealed class FaceRecognitionResult(val faceFileName: String, val faceFilePath: String) {
    data class Recognised(
        private val fileName: String,
        private val filePath: String,
        val estimatedName: String
    ): FaceRecognitionResult(faceFileName = fileName, faceFilePath = filePath)

    data class NotRecognised(
        private val fileName: String,
        private val filePath: String
    ): FaceRecognitionResult(faceFileName = fileName, faceFilePath = filePath)
}