package `in`.surajsau.jisho.data

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CardDataProviderImpl @Inject constructor(): CardDataProvider {

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    override fun identifyTexts(bitmap: Bitmap): Flow<Text> = callbackFlow {
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { trySend(it) }
            .addOnFailureListener {
                it.printStackTrace()
                throw it
            }
            .addOnCompleteListener { close() }

        awaitClose { close() }
    }
}

interface CardDataProvider {

    fun identifyTexts(bitmap: Bitmap): Flow<Text>
}