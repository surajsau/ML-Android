package `in`.surajsau.jisho.data

import `in`.surajsau.jisho.data.model.Screen
import `in`.surajsau.jisho.ui.Destinations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ScreensDataProviderImpl @Inject constructor(): ScreensDataProvider {

    override fun fetchScreen(): Flow<List<Screen>> = flow {
        emit(listOf(
                Screen(
                    destinations = Destinations.DigitalInk,
                    previewImage = "https://github.com/surajsau/ML-Android/raw/main/screenshots/translate_app.gif",
                    title = "On-device Google Translate",
                    description = "Character Recognition & Translation using Google MLKit",
                    tags = listOf("MLKit")
                ),

                Screen(
                    destinations = Destinations.StyleTransfer,
                    previewImage = "",
                    title = "Style Transfer",
                    description = "Character Recognition & Translation using Google MLKit",
                    tags = listOf("MLKit")
                ),

                Screen(
                    destinations = Destinations.Gpt,
                    previewImage = "",
                    title = "Text Suggestion using GPT",
                    description = "Using Huggingface/Transformers GPT model for text completion",
                    tags = listOf("MLKit")
                ),
            )
        )
    }
}

interface ScreensDataProvider {

    fun fetchScreen(): Flow<List<Screen>>
}
