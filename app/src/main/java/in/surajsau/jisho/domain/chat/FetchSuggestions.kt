package `in`.surajsau.jisho.domain.chat

import `in`.surajsau.jisho.data.chat.ReplySuggestionProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class FetchSuggestions @Inject constructor(
    private val replySuggestionProvider: ReplySuggestionProvider
) {

    fun invoke(): Flow<List<String>> {
        return replySuggestionProvider.suggestions
            .receiveAsFlow()
            .map { suggestions -> suggestions.map { it.text } }
    }
}