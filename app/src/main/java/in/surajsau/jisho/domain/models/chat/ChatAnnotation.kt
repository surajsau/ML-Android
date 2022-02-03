package `in`.surajsau.jisho.domain.models.chat

sealed class ChatAnnotation(val tag: String) {

    data class Email(private val id: String, val email: String): ChatAnnotation(tag = id)
    data class Phone(private val id: String, val phone: String): ChatAnnotation(tag = id)
    data class Reminder(private val id: String, val timeStamp: Long): ChatAnnotation(tag = id)
    data class Address(private val id: String, val address: String): ChatAnnotation(tag = id)
}