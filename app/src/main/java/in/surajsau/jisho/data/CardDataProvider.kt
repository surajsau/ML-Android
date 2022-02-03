package `in`.surajsau.jisho.data

import android.graphics.Bitmap
import android.util.Log
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
        TextRecognition.getClient(TextRecognizerOptions.Builder().build())
    }

    override fun identifyTexts(bitmap: Bitmap, language: CardDataProvider.Language): Flow<Text> = callbackFlow {
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener {
                Log.e("Card", it.text)
                trySend(it)
            }
            .addOnFailureListener {
                it.printStackTrace()
                throw it
            }
            .addOnCompleteListener { close() }

        awaitClose { close() }
    }
}

interface CardDataProvider {

    fun identifyTexts(bitmap: Bitmap, language: Language = Language.EN): Flow<Text>

    enum class Language { EN, JP, HI }
}