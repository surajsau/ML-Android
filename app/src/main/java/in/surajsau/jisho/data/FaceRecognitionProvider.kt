package `in`.surajsau.jisho.data

import `in`.surajsau.jisho.ml.Facenet
import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class FaceRecognitionProviderImpl @Inject constructor(private val context: Context): FaceRecognitionProvider {

    private val facenet by lazy { Facenet.newInstance(context) }

    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(160, 160, ResizeOp.ResizeMethod.BILINEAR))
        .add(NormalizeOp())
        .build()

    override suspend fun generateEmbedding(bitmap: Bitmap): FloatArray {
        val input = imageProcessor.process(TensorImage.fromBitmap(bitmap))
        val output = facenet.process(input.tensorBuffer)

        return output.outputFeature0AsTensorBuffer.floatArray
    }

}

class NormalizeOp: TensorOperator {

    override fun apply(input: TensorBuffer): TensorBuffer {
        val pixels = input.floatArray
        val mean = pixels.average().toFloat()
        val standardDeviation = sqrt(pixels.map { pixel -> (pixel - mean).pow(2) }.sum() / pixels.size.toFloat()).let {
            max(it, 1f/ sqrt(pixels.size.toFloat()))
        }

        val normalizedPixels = pixels.map { (it - mean)/standardDeviation }.toFloatArray()
        val output = TensorBufferFloat.createFixedSize(input.shape, DataType.FLOAT32)
        output.loadArray(normalizedPixels)

        return output
    }

}

interface FaceRecognitionProvider {

    suspend fun generateEmbedding(bitmap: Bitmap): FloatArray
}