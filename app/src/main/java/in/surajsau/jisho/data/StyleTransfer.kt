package `in`.surajsau.jisho.data

import `in`.surajsau.jisho.ml.StylePredictorModel
import `in`.surajsau.jisho.ml.StyleTransferModel
import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class StyleTransfer constructor(private val context: Context) {

    private val stylePredictorModel = StylePredictorModel.newInstance(context)
    private val styleTransferModel = StyleTransferModel.newInstance(context)

    fun process(targetImage: Bitmap, styleImage: Bitmap): Bitmap {
        val styleTensor = TensorImage.fromBitmap(styleImage)
        val targetTensor = TensorImage.fromBitmap(targetImage)
        val styleOutput = stylePredictorModel.process(styleTensor).styleBottleneckAsTensorBuffer

        val styleBottleneck = TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32).apply {
            loadBuffer(styleOutput.buffer)
        }

        val output = styleTransferModel.process(targetTensor, styleBottleneck).styledImageAsTensorImage
        return output.bitmap
    }

    companion object {
        private const val BottleNeckSize = 100
        private const val StyleImageSize = 256
        private const val ContentImageSize = 384


    }
}