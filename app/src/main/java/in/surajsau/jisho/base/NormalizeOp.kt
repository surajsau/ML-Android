package `in`.surajsau.jisho.base

import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

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