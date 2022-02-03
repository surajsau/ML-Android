package `in`.surajsau.jisho.domain.chat

import `in`.surajsau.jisho.data.chat.EmojiLabellingDataProvider
import `in`.surajsau.jisho.data.model.Emoji
import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEmojiSuggestions @Inject constructor(
    private val emojiLabellingDataProvider: EmojiLabellingDataProvider
) {

    fun invoke(bitmap: Bitmap): Flow<List<Emoji>>
        = emojiLabellingDataProvider.getEmojis(bitmap)
}