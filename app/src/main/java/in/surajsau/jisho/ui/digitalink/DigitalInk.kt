package `in`.surajsau.jisho.ui.digitalink

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class DigitalInk @Inject constructor() {

    val predictions = Channel<List<String>>(4)

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

    fun checkIfModelIsDownloaded(): Flow<MLKitModelStatus> = callbackFlow {
        trySend(MLKitModelStatus.CheckingDownload)

        this@DigitalInk.remoteModelManager
            .isModelDownloaded(this@DigitalInk.recognitionModel)
            .addOnSuccessListener { isDownloaded ->
                if (isDownloaded)
                    trySend(MLKitModelStatus.Downloaded)
                else
                    trySend(MLKitModelStatus.NotDownloaded)
            }
            .addOnCompleteListener { close() }
            .addOnFailureListener {
                it.printStackTrace()
                close(it)
            }

        awaitClose { cancel() }
    }

    fun downloadModel(): Flow<MLKitModelStatus> = callbackFlow {
        val downloadConditions = DownloadConditions.Builder()
            .build()

        trySend(MLKitModelStatus.Downloading)
        this@DigitalInk.remoteModelManager
            .download(this@DigitalInk.recognitionModel, downloadConditions)
            .addOnSuccessListener {
                trySend(MLKitModelStatus.Downloaded)
            }
            .addOnCompleteListener { close() }
            .addOnFailureListener {
                it.printStackTrace()
                close(it)
            }

        awaitClose { cancel() }
    }

    fun record(x: Float, y: Float) {
        val point = Ink.Point.create(x, y)
        this.strokeBuilder.addPoint(point)
    }

    fun finishRecording() {
        val stroke = this@DigitalInk.strokeBuilder.build()

        val inkBuilder = Ink.builder()
        inkBuilder.addStroke(stroke)

        this@DigitalInk.recognizer.recognize(inkBuilder.build())
            .addOnCompleteListener {
                this@DigitalInk.strokeBuilder = Ink.Stroke.builder()
            }
            .addOnSuccessListener { result -> this.predictions.trySend(result.candidates.map { it.text }) }
            .addOnFailureListener { it.printStackTrace() }
    }

    fun close() {
        this.recognizer.close()
    }
}