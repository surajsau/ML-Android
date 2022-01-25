package `in`.surajsau.jisho.domain.models

import com.soywiz.klock.Date

sealed class IDCard {
    data class Front(
        val membershipNumber: String,
        val name: String,
        val year: String,
        val degree: String,
        val dateOfBirth: Date,
    ): IDCard()

    data class Back(
        val address: String,
        val mobileNumber: String,
        val email: String,
    ): IDCard()
}