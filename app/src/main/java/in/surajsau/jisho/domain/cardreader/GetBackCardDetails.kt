package `in`.surajsau.jisho.domain.cardreader

import `in`.surajsau.jisho.data.CardDataProvider
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.domain.models.CardDetails
import android.util.Log
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
                Log.e("Card", textResult.text)
                val lines = textResult.text.replace(":", "").split("\n")

                val mobileNumber = lines.firstOrNull { it.startsWith("Mob. No.") }
                    ?.split("\\s+".toRegex())
                    ?.firstOrNull { it.matches(MobileNumberRegex) } ?: ""

                val email = lines.firstOrNull { it.startsWith("e-mail") }
                    ?.split(" ")?.getOrNull(1) ?: ""

                val address = lines.subList(
                    fromIndex = lines.indexOfFirst { it.startsWith("Address") },
                    toIndex = lines.indexOfFirst { it.startsWith("Mob.") }
                ).joinToString(separator = "\n")

                CardDetails.Back(
                    address = address,
                    mobileNumber = mobileNumber,
                    email = email
                )
            }

    companion object {
        private val MobileNumberRegex = Regex("(\\+91-)\\d{10}")
    }
}