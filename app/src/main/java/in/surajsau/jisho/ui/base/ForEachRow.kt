package `in`.surajsau.jisho.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
inline fun<T> ForEachRow(
    items: List<T>,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    rowItem: @Composable RowScope.(T) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        items.forEach { rowItem(it) }
    }
}

@Composable
inline fun<T> ForEachRowIndexed(
    items: List<T>,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    rowItem: @Composable RowScope.(Int, T) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        items.forEachIndexed { index, elem -> rowItem(index, elem) }
    }
}

@Preview
@Composable
private fun PreviewForEachRow() {

    Box(modifier = Modifier.fillMaxWidth().background(Color.White).height(100.dp)) {
        ForEachRow(items = (1..3).toList(), modifier = Modifier.padding(4.dp)) {
            Box(modifier = Modifier.size(100.dp).padding(end = 4.dp).background(Color.LightGray)) {
                Text(text = "$it", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}