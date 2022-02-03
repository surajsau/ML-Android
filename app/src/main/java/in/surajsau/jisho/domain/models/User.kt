package `in`.surajsau.jisho.domain.models

data class User(
    private val firstName: String,
    private val lastName: String,
    val profileUrl: String
) {

    val displayName: String
        get() = "$firstName $lastName"
}
