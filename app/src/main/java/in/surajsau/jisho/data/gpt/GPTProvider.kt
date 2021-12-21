package `in`.surajsau.jisho.data.gpt

import `in`.surajsau.jisho.base.reverseMap
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
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

    override fun loadModel(): Flow<MLKitModelStatus> = flow {
        emit(MLKitModelStatus.CheckingDownload)

        val encoder = gptEncoderProvider.loadEncoderMapping()
        val decoder = encoder.reverseMap()
        val bpeTokens = bpeTokenProvider.loadBpeTokens()

        this@GPTProviderImpl.tokenizer = GPTTokenizer(encoder, decoder, bpeTokens)

        this@GPTProviderImpl.interpreter = fileProvider.fetchInterpreter("gpt/gpt2.tflite")
        emit(MLKitModelStatus.Downloaded)
    }

    override fun generate(text: String, maxLength: Int): Flow<String> = flow {
        val tokens = tokenizer.tokenize(text).toMutableList()

        repeat(maxLength) {
            val maxTokens = tokens.takeLast(SequenceLength).toIntArray()

            // ensuring maxTokens if of SequenceLength size
            val paddedTokens = maxTokens + IntArray(SequenceLength - maxTokens.size)

            // input: [1][SequenceLength]
            val inputIds = Array(1){ paddedTokens }

            // output: [1][SequenceLength][VocabSize]
            val predictions = Array(1) { Array(SequenceLength) { FloatArray(VocabSize) } }

            val outputs = mutableMapOf<Int, Any>(0 to predictions)

            interpreter.runForMultipleInputsOutputs(arrayOf(inputIds), outputs)

            val outputLogits = predictions[0][maxTokens.size - 1]

            val filteredLogitsWithIndices = outputLogits
                .mapIndexed { index, logit -> (index to logit) }
                .sortedByDescending { it.second }
                .take(40)

            val filteredLogits = filteredLogitsWithIndices.map { it.second }

            val maxLogitValue = filteredLogits.maxOrNull()!!
            val logitsExp = filteredLogits.map { exp(it - maxLogitValue) }
            val sumOfLogitsExp = logitsExp.sum()
            val probabilities = logitsExp.map { it.div(sumOfLogitsExp) }

            val logitIndices = filteredLogitsWithIndices.map { it.first }
            val nextToken = sample(logitIndices, probabilities)

            tokens.add(nextToken)
            val decodedToken = tokenizer.convertToString(listOf(nextToken))

            emit(decodedToken)
        }
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

    fun loadModel(): Flow<MLKitModelStatus>
    fun generate(text: String, maxLength: Int = 100): Flow<String>
}