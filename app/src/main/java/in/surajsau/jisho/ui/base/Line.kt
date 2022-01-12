package `in`.surajsau.jisho.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Line(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = Color.DarkGray,
    isVertical: Boolean = false,

) {
    Box(
        modifier = modifier.apply {
            if (isVertical)
                width(thickness)
            else
                height(thickness)
        }.background(color)
    )
}