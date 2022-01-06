package `in`.surajsau.jisho.data.gpt

import `in`.surajsau.jisho.base.subListFrom

class GPTTokenizer (
    private val encodingMap: Map<String, Int>,
    private val decodingMap: Map<Int, String>,
    private val bpeTokens: Map<Pair<String, String>, Int>,
) {
    private val encoderRegex = Regex(""""'s|'t|'re|'ve|'m|'ll|'d| ?\p{L}+| ?\p{N}+| ?[^\s\p{L}\p{N}]+|\s+(?!\S)|\s+""")

    fun tokenize(text: String): MutableList<Int> {
        val tokens = encoderRegex.findAll(text).map { result ->
            result.value.codePoints()
                .boxed()
                .map { ByteEncoder[it]!! }
                .toArray()
                .joinToString("")
        }

        return tokens
            .map { bpe(it) }
            .flatten()
            .map { encodingMap[it]!! }
            .toMutableList()
    }

    fun convertToString(tokens: List<Int>): String {
        val text = tokens.joinToString("") { decodingMap.getOrDefault(it, "") }
        val utfCodepoints = text.map { ByteDecoder[it.toString()]!! }
        return String(utfCodepoints.toIntArray(), 0, utfCodepoints.size)
    }

    private fun bpe(token: String): List<String> {
        if (token.length <= 1) return listOf(token)

        var word = token.map { it.toString() }
        var pairs = getPairs(word)

        while (true) {
            if (!pairs.any { bpeTokens.containsKey(it) }) break
            val (first, second) = pairs.minByOrNull { bpeTokens.getOrDefault(it, Int.MAX_VALUE) } ?: break

            var i = 0
            val newWord = mutableListOf<String>()
            while (i < word.size) {
                val j = word.withIndex().indexOfFirst { it.index >= i && it.value == first }
                if (j != -1) {
                    newWord.addAll(word.subList(i, j))
                    i = j
                } else {
                    newWord.addAll(word.subList(i, word.size))
                    break
                }

                if (word[i] == first && i < word.size-1 && word[i+1] == second) {
                    newWord.add(first+second)
                    i += 2
                } else {
                    newWord.add(word[i])
                    i += 1
                }
            }

            word = newWord
            if (word.size == 1) {
                break
            } else {
                pairs = getPairs(word)
            }
        }

        return word
    }

    private fun getPairs(word: List<String>): Set<Pair<String, String>> = mutableSetOf<Pair<String, String>>().apply {
        for (i in 0 until word.size - 1) {
            add(word[i] to word[i+1])
        }
    }
}