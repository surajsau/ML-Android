package `in`.surajsau.jisho.domain.chat

import `in`.surajsau.jisho.data.chat.SmartRepliesProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class FetchSuggestions @Inject constructor(
    private val smartRepliesProvider: SmartRepliesProvider
) {

    fun invoke(): Flow<List<String>> {
        return smartRepliesProvider.suggestions
            .receiveAsFlow()
            .map { suggestions -> suggestions.map { it.text } }
    }
}