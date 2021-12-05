package `in`.surajsau.jisho.domain

import `in`.surajsau.jisho.data.DigitalInkProvider
import javax.inject.Inject

class FinishedRecording @Inject constructor(
    private val digitalInkProvider: DigitalInkProvider
) {

    fun invoke() = digitalInkProvider.finishRecording()

}