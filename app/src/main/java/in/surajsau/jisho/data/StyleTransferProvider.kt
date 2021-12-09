package `in`.surajsau.jisho.data

import `in`.surajsau.jisho.ml.MagentaStylePredictor
import `in`.surajsau.jisho.ml.MagentaStyleTransfer
import `in`.surajsau.jisho.ml.StylePredictorModel
import `in`.surajsau.jisho.ml.StyleTransferModel
import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import javax.inject.Inject

class StyleTransferProviderImpl @Inject constructor(
    private val context: Context,
): StyleTransferProvider {

    private val stylePredictorModel = MagentaStylePredictor.newInstance(context)
    private val styleTransferModel = MagentaStyleTransfer.newInstance(context)

    override fun process(targetImage: Bitmap, styleImage: Bitmap): Flow<StyleTransferProvider.StyleTransferState> = flow {
        emit(StyleTransferProvider.StyleTransferState.Started)

        val styleTensor = TensorImage.fromBitmap(styleImage)
        val targetTensor = TensorImage.fromBitmap(targetImage)
        val styleOutput = stylePredictorModel.process(styleTensor).styleBottleneckAsTensorBuffer

        val styleBottleneck = TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, StyleTransferProvider.BottleNeckSize), DataType.FLOAT32).apply {
            loadBuffer(styleOutput.buffer)
        }

        val output = styleTransferModel.process(targetTensor, styleBottleneck).styledImageAsTensorImage
        emit(StyleTransferProvider.StyleTransferState.Finished(output.bitmap))
    }
}

interface StyleTransferProvider {

    fun process(targetImage: Bitmap, styleImage: Bitmap): Flow<StyleTransferState>

    sealed class StyleTransferState {
        object Idle: StyleTransferState()
        object Started: StyleTransferState()
        data class Finished(val image: Bitmap): StyleTransferState()
    }

    companion object {
        const val BottleNeckSize = 100
        const val StyleImageSize = 256
        const val ContentImageSize = 384

        val Styles = (0..5).map { "style$it.jpg" }
    }
}