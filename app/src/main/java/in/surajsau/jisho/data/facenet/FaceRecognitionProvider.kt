package `in`.surajsau.jisho.data.facenet

import `in`.surajsau.jisho.base.NormalizeOp
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.ml.Facenet
import `in`.surajsau.jisho.ui.digitalink.MLKitModelStatus
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.ContextMenu
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.sqrt

class FaceRecognitionProviderImpl @Inject constructor(
    private val context: Context,
    private val fileProvider: FileProvider,
): FaceRecognitionProvider {

    private lateinit var facenet: Facenet

    val embeddings = mutableMapOf<String, List<FloatArray>>()

    override suspend fun loadEmbeddingFor(faceName: String) {
        val lines = fileProvider.readStringFromFile(
            folderName = FaceRecognitionProvider.FACENET_EMBEDDINGS_FOLDER,
            fileName = faceName
        )
        val embedding = lines
            .filter { it.isNotEmpty() }
            .map { line -> line.split(":").map { it.toFloat() }.toFloatArray() }
        embeddings[faceName] = embedding
    }

    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(160, 160, ResizeOp.ResizeMethod.BILINEAR))
        .add(NormalizeOp())
        .build()

    override suspend fun generateEmbedding(bitmap: Bitmap): FloatArray {
        val input = imageProcessor.process(TensorImage.fromBitmap(bitmap))
        val output = facenet.process(input.tensorBuffer)

        return output.outputFeature0AsTensorBuffer.floatArray
    }

    override suspend fun saveEmbedding(faceName: String, embedding: FloatArray) {
        fileProvider.writeStringToFile(
            folderName = FaceRecognitionProvider.FACENET_EMBEDDINGS_FOLDER,
            fileName = faceName,
            string = embedding.joinToString(":")
        )

        if (embeddings.containsKey(faceName))
            embeddings[faceName]!!.toMutableList().add(embedding)
        else
            embeddings[faceName] = mutableListOf<FloatArray>().apply { add(embedding) }
    }

    override suspend fun compareEmbedding(embedding: FloatArray): Map<String, List<Float>> {
        return embeddings.mapValues { entry -> entry.value.map { cosineSimilarity(embedding, it) }}
    }

    override fun close() {
        facenet.close()
        embeddings.clear()
    }

    override fun initiate() {
        facenet = Facenet.newInstance(context)
    }

    private fun l2Similarity(embedding1: FloatArray, embedding2: FloatArray): Float {
        return sqrt(embedding1.mapIndexed { i, e1 -> (e1 * embedding2[i]).pow(2) }.sum())
    }

    private fun cosineSimilarity(embedding1: FloatArray, embedding2: FloatArray): Float {
        val magnitude1 = sqrt(embedding1.map { it * it }.sum())
        val magnitude2 = sqrt(embedding2.map { it * it }.sum())

        val dot = embedding1.mapIndexed { i, e1 -> e1 * embedding2[i] }.sum()

        return (dot/magnitude1 * magnitude2)
    }

}

interface FaceRecognitionProvider {

    suspend fun loadEmbeddingFor(faceName: String)

    suspend fun saveEmbedding(faceName: String, embedding: FloatArray)

    suspend fun generateEmbedding(bitmap: Bitmap): FloatArray

    suspend fun compareEmbedding(embedding: FloatArray): Map<String, List<Float>>

    fun close()

    fun initiate()

    companion object {
        const val FACENET_EMBEDDINGS_FOLDER = "embeddings/faces/"
    }
}