package `in`.surajsau.jisho.base

fun<T> List<T>.subListFrom(startIndex: Int) = this.subList(startIndex, this.size - 1)

fun<K, V> Map<K, V>.reverseMap(): Map<V, K> = this.entries.associateBy({it.value}, {it.key})