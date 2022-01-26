package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.domain.models.FaceModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import java.io.File

@Composable
fun SavedFace(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
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
                .border(
                    width = if (isSelected) 4.dp else 0.dp,
                    color = Color.DarkGray,
                    shape = CircleShape
                )
                .clipToBounds()
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )

        Text(
            text = model.faceName,
            modifier = Modifier.padding(top = 4.dp),
            fontSize = 12.sp,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun previewSavedFaceImage() {

    SavedFace(
        modifier = Modifier.width(56.dp),
        model = FaceModel("", "John Doe")
    )
}