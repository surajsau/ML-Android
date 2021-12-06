package `in`.surajsau.jisho.data

import `in`.surajsau.jisho.ml.StylePredictorModel
import `in`.surajsau.jisho.ml.StyleTransferModel
import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import javax.inject.Inject

class StyleTransferProviderImpl @Inject constructor(
    private val context: Context,
    private val fileProvider: FileProvider,
): StyleTransferProvider {

    private val stylePredictorModel = StylePredictorModel.newInstance(context)
    private val styleTransferModel = StyleTransferModel.newInstance(context)

    override fun process(targetImagePath: String, styleImagePath: String): Flow<Bitmap> = flow {
        val styleTensor = TensorImage.fromBitmap(fileProvider.fetchBitmap(styleImagePath))
        val targetTensor = TensorImage.fromBitmap(fileProvider.fetchBitmap(targetImagePath))
        val styleOutput = stylePredictorModel.process(styleTensor).styleBottleneckAsTensorBuffer

        val styleBottleneck = TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32).apply {
            loadBuffer(styleOutput.buffer)
        }

        val output = styleTransferModel.process(targetTensor, styleBottleneck).styledImageAsTensorImage
        emit(output.bitmap)
    }

    companion object {
        private const val BottleNeckSize = 100
        private const val StyleImageSize = 256
        private const val ContentImageSize = 384


    }
}

interface StyleTransferProvider {

    fun process(targetImagePath: String, styleImagePath: String): Flow<Bitmap>
}