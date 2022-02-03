package `in`.surajsau.jisho.domain.chat

import `in`.surajsau.jisho.data.chat.EmojiLabellingDataProvider
import javax.inject.Inject

class LoadEmojis @Inject constructor(
    private val emojiLabellingDataProvider: EmojiLabellingDataProvider
) {

    suspend fun invoke() {
        emojiLabellingDataProvider.loadEmojis()
    }
}