package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.base.FileName
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter
import java.io.File

@Composable
fun SavedFaceImage(
    modifier: Modifier = Modifier,
    faceName: String,
    filePath: String,
) {

    Image(
        painter = rememberImagePainter(data = File(filePath)),
        contentDescription = faceName,
        modifier = modifier
            .clip(CircleShape)
            .background(Color.LightGray)
            .clipToBounds(),
        contentScale = ContentScale.Fit
    )
}