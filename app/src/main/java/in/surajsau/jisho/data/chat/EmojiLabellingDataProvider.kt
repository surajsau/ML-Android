package `in`.surajsau.jisho.data.chat

import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.data.model.Emoji
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class EmojiLabellingDataProviderImpl @Inject constructor(
    private val fileProvider: FileProvider
): EmojiLabellingDataProvider {

    private var emojis: List<Emoji> = emptyList()

    private val labeller by lazy {
        ImageLabeling.getClient(ImageLabelerOptions.Builder().build())
    }

    override suspend fun loadEmojis() {
        runCatching {
            val gson = Gson()
            val inputStream = fileProvider.fetchAssetInputStream("emojis.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val listEmojiType = object: TypeToken<List<Emoji>>(){}.type
            emojis = gson.fromJson(jsonString, listEmojiType)
        }.onFailure { it.printStackTrace() }
    }

    override fun getEmojis(bitmap: Bitmap): Flow<List<Emoji>> = callbackFlow {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        labeller.process(inputImage)
            .addOnSuccessListener { labels ->
                val result = mutableListOf<Emoji>()
                labels
                    .filter { label -> label.confidence > 0.4 }
                    .forEach { label ->
                        val emoji = emojis.firstOrNull { it.label == label.text && it.emojis.isNotEmpty() } ?: return@forEach
                        result.add(emoji)
                    }

                trySend(result)
            }
            .addOnFailureListener {
                it.printStackTrace()
                cancel("getEmojis() failed", it)
            }
            .addOnCompleteListener { close() }

        awaitClose { close() }
    }

}

interface EmojiLabellingDataProvider {

    suspend fun loadEmojis()

    fun getEmojis(bitmap: Bitmap): Flow<List<Emoji>>
}