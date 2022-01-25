package `in`.surajsau.jisho.domain.cardreader

import `in`.surajsau.jisho.base.Optional
import `in`.surajsau.jisho.data.CardDataProvider
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.domain.models.CardDetails
import android.util.Log
import android.util.Patterns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/*
    Card No. : S00150
    Date of Issue : June.23, 2015
    Address for correspondence:
    Qr. No. - 413 C, Sector 4,
    Ukkunagaram Township,
    VISAKHAPATNAM - 530032
    Mob. No. : +91- 7829953236
    e-mail: i@surajsau.in
    2015010006
 */
class GetBackCardDetails @Inject constructor(
    private val cardDataProvider: CardDataProvider,
    private val fileProvider: FileProvider
) {

    fun invoke(fileName: String): Flow<CardDetails.Back>
        = flow { emit(fileProvider.fetchCachedBitmap(fileName = fileName)) }
            .flatMapLatest { cardDataProvider.identifyTexts(it) }
            .map { textResult ->
                val lines = textResult.text.replace(":", "").split("\n")

                Log.e("Card", lines.joinToString("\n"))

//                val mobileNumber = lines.firstOrNull { it.startsWith("Mob. No.") }?.let {
//                    return@let MobileNumberRegex.find(it)?.value
//                } ?: ""

                val mobileNumber = lines
                    .map { Optional.of(MobileNumberRegex.find(it)?.value) }
                    .first { it !is Optional.Empty }

                val email = lines
                    .map { Optional.of(EmailRegex.find(it)?.value) }
                    .first { it !is Optional.Empty }

                val address = lines.subList(
                    fromIndex = lines.indexOfFirst { it.startsWith("Address") } + 1,
                    toIndex = lines.indexOfFirst { it.startsWith("Mob.") }
                ).joinToString(separator = "\n")

                CardDetails.Back(
                    address = address,
                    mobileNumber = mobileNumber.getValue(),
                    email = email.getValue()
                )
            }

    companion object {
        private val MobileNumberRegex = Regex("(\\+91-\\s+)\\d{10}")
        private val EmailRegex = Patterns.EMAIL_ADDRESS.pattern().toRegex()
    }
}