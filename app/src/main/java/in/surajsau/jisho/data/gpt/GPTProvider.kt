package `in`.surajsau.jisho.data.gpt

import `in`.surajsau.jisho.base.reverseMap
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.yield
import org.tensorflow.lite.Interpreter
import javax.inject.Inject
import kotlin.math.exp
import kotlin.random.Random

/**
 * Reference:
 * https://github.com/huggingface/tflite-android-transformers/blob/master/gpt2/src/main/java/co/huggingface/android_transformers/gpt2/ml/GPT2Client.kt
 */

class GPTProviderImpl @Inject constructor(
    private val gptEncoderProvider: GPTEncoderProvider,
    private val bpeTokenProvider: BpeTokenProvider,
    private val fileProvider: FileProvider,
): GPTProvider {

    private lateinit var interpreter: Interpreter

    private lateinit var tokenizer: GPTTokenizer

    override val suggestion: Channel<String> = Channel()

    override fun loadModel(): Flow<MLKitModelStatus> = flow {
        emit(MLKitModelStatus.CheckingDownload)

        val encoder = gptEncoderProvider.loadEncoderMapping()
        val decoder = encoder.reverseMap()
        val bpeTokens = bpeTokenProvider.loadBpeTokens()

        this@GPTProviderImpl.tokenizer = GPTTokenizer(encoder, decoder, bpeTokens)

        this@GPTProviderImpl.interpreter = fileProvider.fetchInterpreter("gpt/model.tflite")
        emit(MLKitModelStatus.Downloaded)
    }

    override suspend fun generate(text: String, maxLength: Int) {
        val tokens = tokenizer.tokenize(text)
        var result = ""

        repeat (maxLength) {
            val maxTokens    = tokens.takeLast(SequenceLength).toIntArray()
            val paddedTokens = maxTokens + IntArray(SequenceLength - maxTokens.size)
            val inputIds     = Array(1) { paddedTokens }

            val predictions = Array(1) { Array(SequenceLength) { FloatArray(VocabSize) } }
            val outputs = mutableMapOf<Int, Any>(0 to predictions)

            interpreter.runForMultipleInputsOutputs(arrayOf(inputIds), outputs)
            val outputLogits = predictions[0][maxTokens.size-1]

            val filteredLogitsWithIndexes = outputLogits
                .mapIndexed { index, fl -> (index to fl) }
                .sortedByDescending { it.second }
                .take(40)

            // Softmax computation on filtered logits
            val filteredLogits = filteredLogitsWithIndexes.map { it.second }
            val maxLogitValue  = filteredLogits.maxOrNull()!!
            val logitsExp      = filteredLogits.map { exp(it - maxLogitValue) }
            val sumExp         = logitsExp.sum()
            val probs          = logitsExp.map { it.div(sumExp) }

            val logitsIndexes = filteredLogitsWithIndexes.map { it.first }
            val nextToken = sample(logitsIndexes, probs)

            tokens.add(nextToken)
            val decodedToken = tokenizer.convertToString(listOf(nextToken))
            result += decodedToken
        }

        suggestion.trySend(result)
    }

    private fun randomIndex(probabilities: List<Float>): Int {
        val random = probabilities.sum() * Random.nextFloat()
        var accumulator = 0f

        probabilities.forEachIndexed { index, probability ->
            accumulator += probability
            if (random < accumulator)
                return index
        }

        return probabilities.size - 1
    }

    private fun sample(indices: List<Int>, probabilities: List<Float>): Int {
        val randomIndex = randomIndex(probabilities)
        return indices[randomIndex]
    }

    companion object {
        const val SequenceLength = 64
        const val VocabSize = 50257
    }

}

interface GPTProvider {

    val suggestion: Channel<String>

    fun loadModel(): Flow<MLKitModelStatus>
    suspend fun generate(text: String, maxLength: Int = 100)
}