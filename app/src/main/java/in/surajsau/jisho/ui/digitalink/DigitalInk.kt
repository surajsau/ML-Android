package `in`.surajsau.jisho.ui.digitalink

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.*
import javax.inject.Inject

enum class MLKitModelStatus {
    Downloaded, CheckingDownload, Downloading
}

class DigitalInk @Inject constructor() {

    private var strokeBuilder: Ink.Stroke.Builder = Ink.Stroke.builder()

    private val recognitionModel = DigitalInkRecognitionModel
            .builder(DigitalInkRecognitionModelIdentifier.JA)
            .build()

    private val remoteModelManager = RemoteModelManager.getInstance()

    private val recognizer = DigitalInkRecognition.getClient(
        DigitalInkRecognizerOptions
            .builder(this.recognitionModel)
            .build()
    )

    private var writingArea: WritingArea? = null

    fun checkIfModelIsDownloaded(onStatusChanged: (MLKitModelStatus) -> Unit) {
        onStatusChanged.invoke(MLKitModelStatus.CheckingDownload)

        val downloadConditions = DownloadConditions.Builder()
            .build()

        this.remoteModelManager
            .isModelDownloaded(this.recognitionModel)
            .addOnSuccessListener { isDownloaded ->
                if (isDownloaded)
                    onStatusChanged.invoke(MLKitModelStatus.Downloaded)
                else {
                    onStatusChanged.invoke(MLKitModelStatus.Downloading)
                    this.remoteModelManager
                        .download(this.recognitionModel, downloadConditions)
                        .addOnSuccessListener {
                            onStatusChanged.invoke(MLKitModelStatus.Downloaded)
                        }
                        .addOnFailureListener { it.printStackTrace() }
                }
            }
            .addOnFailureListener { it.printStackTrace() }
    }

    fun setWritingArea(width: Float, height: Float) {
        this.writingArea = WritingArea(width, height)
    }

    fun record(x: Float, y: Float) {
        val point = Ink.Point.create(x, y)
        this.strokeBuilder.addPoint(point)
    }

    fun finishRecording(onPredicted: (List<String>) -> Unit) {
        val stroke = this.strokeBuilder.build()

        val inkBuilder = Ink.builder()
        inkBuilder.addStroke(stroke)

        this@DigitalInk.recognizer.recognize(inkBuilder.build())
            .addOnCompleteListener { this.strokeBuilder = Ink.Stroke.builder() }
            .addOnSuccessListener { result -> onPredicted.invoke(result.candidates.map { it.text }) }
            .addOnFailureListener { it.printStackTrace() }
    }
}