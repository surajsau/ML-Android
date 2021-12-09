package `in`.surajsau.jisho.base

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotate(degree: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degree.toFloat())
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}