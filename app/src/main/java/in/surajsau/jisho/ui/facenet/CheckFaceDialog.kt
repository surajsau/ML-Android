package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.domain.models.FaceRecognitionResult
import `in`.surajsau.jisho.ui.base.Line
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
fun CheckFaceDialog(
    modifier: Modifier = Modifier,
    results: List<FaceRecognitionResult>,
    onNameConfirmed: (isNewFace: Boolean, faceFileName: String, name: String) -> Unit,
    onDismiss: () -> Unit
) {

    var currentIndex by remember { mutableStateOf(0) }

    val currentResult by derivedStateOf { results[currentIndex] }

    var currentName by remember { mutableStateOf("") }

    LaunchedEffect(currentResult) {
        // initiate currentName value based on currentResult
        currentName = (currentResult as? FaceRecognitionResult.Recognised)?.estimatedName ?: ""
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Column(
            modifier = modifier.background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (currentIndex > 0)
                    IconButton(
                        onClick = { currentIndex-- }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                Image(
                    painter = rememberImagePainter(data = File(currentResult.faceFilePath)),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .clipToBounds()
                        .clip(RoundedCornerShape(4.dp))
                        .size(200.dp),
                    contentScale = ContentScale.Inside,
                )

                if (currentIndex < results.size - 1)
                    IconButton(
                        onClick = { currentIndex++ }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight,
                            contentDescription = "Next",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
            }

            when (currentResult) {
                is FaceRecognitionResult.Recognised -> {
                    Text(
                        text = (currentResult as FaceRecognitionResult.Recognised).estimatedName,
                        modifier = Modifier.padding(8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f),
                            onClick = {
                                onNameConfirmed.invoke(false, currentResult.faceFileName, currentName)
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Transparent,
                            ),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                            ),
                            border = BorderStroke(width = 2.dp, color = Color.Green)
                        ) {
                            Text(text = "Yes")
                        }

                        Button(
                            modifier = Modifier.padding(16.dp).weight(1f),
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
                }

                is FaceRecognitionResult.NotRecognised -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                            value = currentName,
                            onValueChange = { currentName = it }
                        )

                        IconButton(onClick = {
                            onNameConfirmed.invoke(true, currentResult.faceFileName, currentName) }
                        ) {
                            Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Confirm")
                        }
                    }
                }
            }

            Line(modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
            )

            Button(
                onClick = { onDismiss.invoke() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                ),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                ),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Done")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}