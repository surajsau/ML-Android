package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.domain.models.FaceModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import java.io.File

@Composable
fun SavedFace(
    modifier: Modifier = Modifier,
    model: FaceModel,
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberImagePainter(data = File(model.imageFilePath)),
            contentDescription = model.faceName,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.LightGray)
                .clipToBounds(),
            contentScale = ContentScale.Fit
        )

        Text(
            text = model.faceName,
            modifier = Modifier.padding(top = 4.dp),
            fontSize = 12.sp
        )
    }
}