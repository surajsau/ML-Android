package `in`.surajsau.jisho.domain.cardreader

import `in`.surajsau.jisho.data.CardDataProvider
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.domain.cardreader.processor.TextProcessor
import `in`.surajsau.jisho.domain.models.CardDetails
import android.util.Log
import androidx.core.text.isDigitsOnly
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import com.soywiz.klock.parseDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetCardDetails<T> @Inject constructor(
    private val textProcessor: TextProcessor<T>,
    private val cardDataProvider: CardDataProvider,
    private val fileProvider: FileProvider
) {

    fun invoke(fileName: String): Flow<T>
            = flow { emit(fileProvider.fetchCachedBitmap(fileName = fileName)) }
        .flatMapLatest { cardDataProvider.identifyTexts(it) }
        .map { textResult -> textProcessor.process(textResult) }


}