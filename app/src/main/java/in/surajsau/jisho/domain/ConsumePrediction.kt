package `in`.surajsau.jisho.domain

import `in`.surajsau.jisho.data.DigitalInkProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import javax.inject.Inject

class ConsumePrediction @Inject constructor(private val digitalInkProvider: DigitalInkProvider) {

    fun invoke(): Flow<List<String>> = digitalInkProvider.predictions.consumeAsFlow()
}