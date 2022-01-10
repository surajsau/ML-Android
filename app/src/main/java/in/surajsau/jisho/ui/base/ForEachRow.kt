package `in`.surajsau.jisho.ui.base

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun<T> List<T>.forEachRow(modifier: Modifier, rowItem: @Composable (T) -> Unit) {
    Row(modifier = modifier) {
        this@forEachRow.forEach { rowItem(it) }
    }
}