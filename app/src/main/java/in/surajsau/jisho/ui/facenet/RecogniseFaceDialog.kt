package `in`.surajsau.jisho.ui.facenet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberImagePainter
import java.io.File

@Composable
fun RecogniseFaceDialog(
    filePath: String,
    estimatedName: String,
    modifier: Modifier = Modifier,
    onNameFinalised: (String) -> Unit,
    onDismiss: () -> Unit,
) {

    var input by remember { mutableStateOf("") }

    LaunchedEffect(estimatedName) { input = estimatedName }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberImagePainter(data = File(filePath)),
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .clipToBounds()
                    .size(200.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Inside,
            )

            Text(
                text = "$estimatedName?",
                modifier = Modifier.padding(16.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { onNameFinalised.invoke(input) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                    )
                ) {
                    Text(text = "Yes")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { onDismiss.invoke() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                    )
                ) {
                    Text(text = "No")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}