package `in`.surajsau.jisho.base

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect

fun Bitmap.rotate(degree: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degree.toFloat())
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Rect.scale(factor: Float): Rect {
    val left = (this.left * factor).toInt()
    val right = (left + (this.width() * factor).toInt())
    val top = (this.top * factor).toInt()
    val bottom = (top + (this.height() * factor).toInt())

    return Rect(left, top, right, bottom)
}