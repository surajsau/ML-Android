package `in`.surajsau.jisho.data.model

sealed class Suggestion {
    object Interpreting: Suggestion()
    data class Message(val value: String): Suggestion()
}
