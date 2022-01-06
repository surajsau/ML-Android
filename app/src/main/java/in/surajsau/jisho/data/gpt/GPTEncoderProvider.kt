package `in`.surajsau.jisho.data.gpt

import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
import android.util.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class GPTEncoderProvider @Inject constructor(private val fileProvider: FileProvider) {

    suspend fun loadEncoderMapping(): Map<String, Int> {
        return withContext(Dispatchers.IO) {
            val inputStream = fileProvider.fetchAssetInputStream("gpt/vocab.json")
            hashMapOf<String, Int>().apply {
                inputStream.use {
                    val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
                    reader.beginObject()
                    while (reader.hasNext()) {
                        put(reader.nextName(), reader.nextInt())
                    }
                    inputStream.close()
                }
            }
        }
    }
}