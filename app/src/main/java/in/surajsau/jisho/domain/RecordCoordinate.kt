package `in`.surajsau.jisho.domain

import `in`.surajsau.jisho.data.DigitalInkProvider
import javax.inject.Inject

class RecordCoordinate @Inject constructor(
    private val digitalInkProvider: DigitalInkProvider
) {

    fun invoke(x: Float, y: Float) = digitalInkProvider.record(x, y)
}