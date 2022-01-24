package `in`.surajsau.jisho.domain.models

import com.soywiz.klock.Date

sealed class CardDetails {
    data class Front(
        val membershipNumber: String,
        val name: String,
        val year: String,
        val degree: String,
        val dateOfBirth: Date,
    ): CardDetails()

    data class Back(
        val address: String,
        val mobileNumber: String,
        val email: String,
    ): CardDetails()
}