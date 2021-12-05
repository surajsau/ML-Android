package `in`.surajsau.jisho.domain

import `in`.surajsau.jisho.data.TranslatorProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import javax.inject.Inject

class ConsumeTranslation @Inject constructor(
    private val translatorProvider: TranslatorProvider
) {

    fun invoke(): Flow<String> = translatorProvider.translation.consumeAsFlow()
}