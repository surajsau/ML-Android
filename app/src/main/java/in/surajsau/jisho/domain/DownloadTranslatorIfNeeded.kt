package `in`.surajsau.jisho.domain

import `in`.surajsau.jisho.data.TranslatorProvider
import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DownloadTranslatorIfNeeded @Inject constructor(
    private val translatorProvider: TranslatorProvider
) {

    fun invoke(): Flow<MLKitModelStatus> = translatorProvider.checkIfModelIsDownloaded()

}