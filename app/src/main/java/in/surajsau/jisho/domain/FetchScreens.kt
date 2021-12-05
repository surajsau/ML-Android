package `in`.surajsau.jisho.domain

import `in`.surajsau.jisho.data.ScreensDataProvider
import `in`.surajsau.jisho.data.model.Screen
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchScreens @Inject constructor(
    private val dataProvider: ScreensDataProvider
) {

    fun invoke(): Flow<List<Screen>> = dataProvider.fetchScreen()
}