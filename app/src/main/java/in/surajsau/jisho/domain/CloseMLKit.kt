package `in`.surajsau.jisho.domain

import `in`.surajsau.jisho.data.DigitalInkProvider
import `in`.surajsau.jisho.data.TranslatorProvider
import javax.inject.Inject

class CloseMLKit @Inject constructor(
    private val digitalInkProvider: DigitalInkProvider,
    private val translatorProvider: TranslatorProvider
) {
    fun invoke() {
        digitalInkProvider.close()
        translatorProvider.close()
    }
}