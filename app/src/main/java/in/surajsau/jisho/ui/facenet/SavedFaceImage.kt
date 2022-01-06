package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.base.FileName
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import coil.compose.rememberImagePainter

@Composable
fun SavedFaceImage(
    modifier: Modifier = Modifier,
    faceName: String,
    fileName: FileName,
) {

    val imagePainter = rememberImagePainter(
        data = Uri.parse(fileName.value)
    )

    Image(
        painter = imagePainter,
        contentDescription = faceName,
        modifier = modifier
            .clip(CircleShape)
            .clipToBounds()
    )
}