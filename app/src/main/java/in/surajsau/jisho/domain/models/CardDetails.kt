package `in`.surajsau.jisho.domain.models

sealed class CardDetails {
    data class Front(
        val membershipNumber: String,
        val name: String,
        val year: String,
        val degree: String,
        val dateOfBirth: String,
    ): CardDetails()

    data class Back(
        val address: String,
        val mobileNumber: String,
        val email: String,
    ): CardDetails()
}