package `in`.surajsau.jisho.data

import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class Translator @Inject constructor() {

    val translation = Channel<String>(4)

    private val translatorOptions = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.JAPANESE)
        .setTargetLanguage(TranslateLanguage.ENGLISH)
        .build()

    private val translator = Translation.getClient(this.translatorOptions)

    fun checkIfModelIsDownloaded(): Flow<MLKitModelStatus> = callbackFlow {
        trySend(MLKitModelStatus.CheckingDownload)
        val downloadConditions = DownloadConditions.Builder()
            .build()

        this@Translator.translator.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                trySend(MLKitModelStatus.Downloaded)
            }
            .addOnCompleteListener { close() }
            .addOnFailureListener {
                it.printStackTrace()
                close(it)
            }

        awaitClose { cancel()  }
    }

    fun translate(text: String) {
        this.translator.translate(text)
            .addOnSuccessListener { this.translation.trySend(it) }
            .addOnFailureListener { it.printStackTrace() }
    }

    fun close() {
        this.translator.close()
    }
}