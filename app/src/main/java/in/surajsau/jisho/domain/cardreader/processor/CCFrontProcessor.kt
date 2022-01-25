package `in`.surajsau.jisho.domain.cardreader.processor

import `in`.surajsau.jisho.domain.models.CreditCard
import android.util.Log
import com.google.mlkit.vision.text.Text
import com.soywiz.klock.DateTime

class CCFrontProcessor : TextProcessor<CreditCard.Front> {

    override fun process(textResult: Text): CreditCard.Front {
        val lines = textResult.text
        Log.e("Card", lines)
        return CreditCard.Front("", DateTime.now().date, "")
    }
}