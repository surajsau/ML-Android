package `in`.surajsau.jisho.domain

import `in`.surajsau.jisho.data.TranslatorProvider
import javax.inject.Inject

class FetchTranslation @Inject constructor(
    private val translatorProvider: TranslatorProvider
) {

    fun invoke(text: String) = translatorProvider.translate(text)
}