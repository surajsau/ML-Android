package `in`.surajsau.jisho.domain.cardreader

import `in`.surajsau.jisho.data.CardDataProvider
import `in`.surajsau.jisho.data.FileProvider
import `in`.surajsau.jisho.domain.models.CardDetails
import android.util.Log
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/*
    Sample:
    -------
    ALUMNI ASSOCIATION
    INDIAN INSTITUTE OF TECHNOLOGY ROORKEE
    (Formerly University of Roorkee))
    ROORKEE -247667, UTTRAKHAND, INDIA
    Membership No. : 2015010006
    Name
    :Suraj Kumar Sau
    Year
    :2015
    Degree
    : B.Tech (ECE)
    Date of Birth :26-04-1994
    Issuing Au
    Hon. Secretary
    Signature of Card Holder

 */
class GetFrontCardDetails @Inject constructor(
    private val cardDataProvider: CardDataProvider,
    private val fileProvider: FileProvider
) {

    fun invoke(fileName: String): Flow<CardDetails.Front>
            = flow { emit(fileProvider.fetchCachedBitmap(fileName = fileName)) }
        .flatMapLatest { cardDataProvider.identifyTexts(it) }
        .map { textResult ->
            val lines = textResult
                .text
                .replace(":", "")
                .split("\n")
                .filterNot {
                    it.startsWith("ALUMNI") or
                            it.startsWith("INDIAN") or
                            it.startsWith("(Formerly") or
                            it.startsWith("ROORKEE") or
                            it.startsWith("Issuing") or
                            it.startsWith("Hon.") or
                            it.startsWith("Signature") or
                            it.startsWith("Date") or
                            it.startsWith("Degree") or
                            it.startsWith("Name")
                }.map { it.trim() }

            Log.e("Card", lines.joinToString("\n"))

            val words = lines.flatMap { it.split(" ") }

            val year = words.firstOrNull { it.isDigitsOnly() && it.length == 4 } ?: ""

            val dateOfBirth = words.firstOrNull { it.matches(DateOfBirthRegex) } ?: ""

            val membershipNumber = words.firstOrNull { it.isDigitsOnly() && it.length > 4 } ?: ""

            val name = lines.firstOrNull { it.matches("[a-zA-Z\\s]+".toRegex()) and (it.split(" ").size > 1) } ?: ""

            val degree = lines.firstOrNull { it.matches(DegreeRegex) } ?: ""

            CardDetails.Front(
                membershipNumber = membershipNumber,
                name = name,
                year = year,
                dateOfBirth = dateOfBirth,
                degree = degree
            )
        }

    companion object {
        private val DateOfBirthRegex = Regex("(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-\\d{4}")
        private val DegreeRegex = Regex("([BM])\\.([A-Za-z]+)\\s\\(([A-Z]+)\\)")
    }
}