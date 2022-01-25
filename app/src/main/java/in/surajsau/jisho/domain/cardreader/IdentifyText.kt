package `in`.surajsau.jisho.domain.cardreader

import `in`.surajsau.jisho.data.CardDataProvider
import `in`.surajsau.jisho.data.FileProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IdentifyText @Inject constructor(
    private val cardDataProvider: CardDataProvider,
    private val fileProvider: FileProvider
) {

    fun invoke(fileName: String, language: CardDataProvider.Language = CardDataProvider.Language.EN): Flow<String>
        = flow { emit(fileProvider.fetchCachedBitmap(fileName)) }
            .flatMapLatest { cardDataProvider.identifyTexts(it, language) }
            .map { it.text }
}