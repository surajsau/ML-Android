package `in`.surajsau.jisho.domain.chat

import `in`.surajsau.jisho.data.chat.EntityExtractionProvider
import javax.inject.Inject

class CheckEntityExtractorAvailability @Inject constructor(
    private val entityExtractionProvider: EntityExtractionProvider
) {

    fun invoke()  = entityExtractionProvider.initModel()
}