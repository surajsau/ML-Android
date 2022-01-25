package `in`.surajsau.jisho.domain.models

import com.soywiz.klock.Date

sealed class CreditCard {

    data class Front(
        val cardNumber: String,
        val expiryDate: Date,
        val cardHolderName: String,
    ): CreditCard()

    data class Back(val cvv: String): CreditCard()
}
