package `in`.surajsau.jisho.domain.models

data class CardDetails(
    val cardNumber: String,
    val expiry: String,
    val cvv: String,
    val cardHolderName: String,
)