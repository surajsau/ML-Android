package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.base.getUriForImage
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberImagePainter
import com.google.mlkit.vision.face.Face
import java.io.File

@Composable
fun AddFaceDialog(
    modifier: Modifier = Modifier,
    filePath: String,
    face: Face,
    onNameAdded: (String) -> Unit,
    onDismiss: () -> Unit
) {

    val painter = rememberImagePainter(data = File(filePath))

    var input by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Column(
            modifier = modifier.background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(x = face.boundingBox.top.toFloat(), y = face.boundingBox.left.toFloat()),
                        size = Size(width = face.boundingBox.width().toFloat(), height = face.boundingBox.height().toFloat())
                    )
                }
            }

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = input,
                onValueChange = { input = it }
            )

            Button(onClick = { onNameAdded.invoke(input) }) { Text(text = "Add Face") }

            Button(
                onClick = { onDismiss.invoke() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                ),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                )
            ) {
                Text(text = "Cancel")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}