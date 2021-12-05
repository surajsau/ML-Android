package `in`.surajsau.jisho.data.model

import `in`.surajsau.jisho.ui.Destinations

data class Screen(
    val destinations: Destinations,
    val previewImage: String = "",
    val title: String,
    val description: String,
    val tags: List<String> = emptyList()
)