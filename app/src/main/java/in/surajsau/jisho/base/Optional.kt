package `in`.surajsau.jisho.base

sealed class Optional<out T> {

    data class Some<out T>(val data: T): Optional<T>()
    object Empty: Optional<Nothing>()

    fun<T> getValue(): T {
        if (this is Empty) {
            throw Exception("$this is empty")
        }
        return (this as? Some<T>)?.data!!
    }
}