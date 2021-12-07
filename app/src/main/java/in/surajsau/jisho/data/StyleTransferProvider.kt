package `in`.surajsau.jisho.data

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

    private val stylePredictorModel = StylePredictorModel.newInstance(context)
    private val styleTransferModel = StyleTransferModel.newInstance(context)

    override fun process(targetImage: Bitmap, styleImage: Bitmap): Flow<StyleTransferProvider.StyleTransferState> = flow {
        emit(StyleTransferProvider.StyleTransferState.Started)

        val styleTensor = TensorImage.fromBitmap(styleImage)
        val targetTensor = TensorImage.fromBitmap(targetImage)
        val styleOutput = stylePredictorModel.process(styleTensor).styleBottleneckAsTensorBuffer

        val styleBottleneck = TensorBuffer.createFixedSize(intArrayOf(1, 1, 1, 100), DataType.FLOAT32).apply {
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
        private const val BottleNeckSize = 100
        private const val StyleImageSize = 256
        private const val ContentImageSize = 384

        val Styles = listOf("scream.png", "van_gogh.png")
    }
}