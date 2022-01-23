package `in`.surajsau.jisho.domain.cardreader

import `in`.surajsau.jisho.data.CardDataProvider
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.domain.models.CardDetails
import android.util.Log
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCardDetails @Inject constructor(
    private val cardDataProvider: CardDataProvider,
    private val fileProvider: FileProvider
) {

    fun invoke(fileName: String): Flow<CardDetails>
        = flow { emit(fileProvider.fetchCachedBitmap(fileName = fileName)) }
            .flatMapLatest { cardDataProvider.identifyTexts(it) }
            .map { textResult ->
                val lines = textResult.text.split("\n")

                val cardNumber = lines
                    .filter { it.contains(" ") }
                    .filter { it.replace(" ", "").isDigitsOnly() }
                    .firstOrNull() ?: ""

                val ownerName = lines
                    .filter { it.contains(" ") and it.none { char -> char.isDigit() } }
                    .maxByOrNull { it.length } ?: ""

                val expiry = lines
                    .filter { it.contains("/") }
                    .lastOrNull() ?: ""

                CardDetails(cardNumber, expiry, "", ownerName)
            }
}