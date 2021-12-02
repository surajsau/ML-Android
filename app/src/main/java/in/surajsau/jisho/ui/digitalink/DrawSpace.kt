package `in`.surajsau.jisho.ui.digitalink

import `in`.surajsau.jisho.ui.theme.DigitalInkColors
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import kotlin.math.abs

sealed class DrawEvent {
    data class Down(val x: Float, val y: Float): DrawEvent()
    data class Move(val x: Float, val y: Float): DrawEvent()
    object Up: DrawEvent()
}

private sealed class DrawPath {
    data class MoveTo(val x: Float, val y: Float): DrawPath()
    data class CurveTo(val prevX: Float, val prevY: Float, val x: Float, val y: Float): DrawPath()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawSpace(
    modifier: Modifier = Modifier,
    reset: Boolean = false,
    onDrawEvent: (DrawEvent) -> Unit,
) {

    val path = remember { Path() }

    var drawPath by remember { mutableStateOf<DrawPath?>(null) }

    if (reset) {
        drawPath = null
        path.reset()
    }

    Canvas(
        modifier = modifier
            .background(Color.White)
            .pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        drawPath = DrawPath.MoveTo(event.x, event.y)
                        onDrawEvent.invoke(DrawEvent.Down(event.x, event.y))
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val prevX = when (drawPath) {
                            is DrawPath.MoveTo -> (drawPath as DrawPath.MoveTo).x
                            is DrawPath.CurveTo -> (drawPath as DrawPath.CurveTo).x

                            else -> 0f
                        }

                        val prevY = when (drawPath) {
                            is DrawPath.MoveTo -> (drawPath as DrawPath.MoveTo).y
                            is DrawPath.CurveTo -> (drawPath as DrawPath.CurveTo).y

                            else -> 0f
                        }
                        drawPath = DrawPath.CurveTo(prevX, prevY, event.x, event.y)
                        onDrawEvent.invoke(DrawEvent.Move(event.x, event.y))
                    }
                    MotionEvent.ACTION_UP -> {
                        onDrawEvent.invoke(DrawEvent.Up)
                    }

                    else -> { /* do nothing */ }
                }

                return@pointerInteropFilter true
            }
    ) {
        if (drawPath == null)
            return@Canvas

        when (drawPath) {
            is DrawPath.MoveTo -> {
                val (x, y) = drawPath as DrawPath.MoveTo
                path.moveTo(x, y)
            }

            is DrawPath.CurveTo -> {
                val (prevX, prevY, x, y) = drawPath as DrawPath.CurveTo
                path.quadraticBezierTo(prevX, prevY, (x + prevX)/2, (y + prevY)/2)
            }
        }

        drawPath(
            path = path,
            color = DigitalInkColors.Stroke,
            style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}