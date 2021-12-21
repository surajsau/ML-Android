package `in`.surajsau.jisho.data.gpt

import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class BpeTokenProvider @Inject constructor(private val fileProvider: FileProvider) {

    suspend fun loadBpeTokens(): Map<Pair<String, String>, Int> {
        return withContext(Dispatchers.IO) {
            val inputStream = fileProvider.fetchAssetInputStream("gpt/merges.txt")

            hashMapOf<Pair<String, String>, Int>().apply {
                inputStream.use {
                    val reader = BufferedReader(InputStreamReader(inputStream,))
                    reader.useLines { seq ->
                        seq.drop(1).forEachIndexed { index, line ->
                            val splits = line.split(" ")
                            put(splits[0] to splits[1], index)
                        }
                    }
                    inputStream.close()
                }
            }
        }
    }
}