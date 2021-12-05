package `in`.surajsau.jisho.domain

import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.data.StyleTransfer
import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

class ProcessImage @Inject constructor(
    private val styleTransfer: StyleTransfer,
    private val fileProvider: FileProvider
) {

    fun invoke(targetImagePath: String, styleImagePath: String): Flow<Bitmap> {
        return fileProvider.fetchBitmap(targetImagePath).zip(fileProvider.fetchBitmap(styleImagePath)) { target, style ->
            styleTransfer.process(targetImage = target, styleImage = style)
        }
    }
}