package `in`.surajsau.jisho.domain.cardreader.processor

import com.google.mlkit.vision.text.Text

interface TextProcessor<T> {
    fun process(textResult: Text): T
}