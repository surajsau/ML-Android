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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CheckFaceDialog(
    modifier: Modifier = Modifier,
    results: List<FaceRecognitionResult>,
    faceNames: List<String>,
    onNameConfirmed: (isNewFace: Boolean, faceFileName: String, name: String) -> Unit,
    onNotConfirmed: (faceFileName: String) -> Unit,
    onDismiss: () -> Unit
) {

    var currentIndex by remember { mutableStateOf(0) }

    val currentResult by derivedStateOf { results[currentIndex] }

    var unansweredCount by remember { mutableStateOf(results.size) }

    DisposableEffect(unansweredCount){
        if (unansweredCount == 0)
            onDismiss.invoke()

        onDispose{}
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
                Box(modifier = Modifier.width(48.dp)) {
                    if (currentIndex > 0) {
                        IconButton(
                            onClick = { currentIndex-- }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                Image(
                    painter = rememberImagePainter(data = File(currentResult.faceFilePath)),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .clipToBounds()
                        .clip(RoundedCornerShape(8.dp))
                        .size(200.dp),
                    contentScale = ContentScale.Inside,
                )

                Box(modifier = Modifier.width(48.dp)) {
                    if (currentIndex < results.size - 1)
                        IconButton(onClick = { currentIndex++ }) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowRight,
                                contentDescription = "Next",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                }
            }

            when (currentResult) {
                is FaceRecognitionResult.Recognised -> {
                    val suggestedName = (currentResult as FaceRecognitionResult.Recognised).estimatedName
                    FaceRecognised(
                        suggestedName = suggestedName,
                        onSuggestionAccepted = { isAccepted ->
                            if (isAccepted)
                                onNameConfirmed.invoke(false, currentResult.faceFileName, suggestedName)
                            else
                                onNotConfirmed.invoke(currentResult.faceFileName)
                            unansweredCount--
                        }
                    )
                }

                is FaceRecognitionResult.NotRecognised -> FaceNotRecognised(
                    modifier = Modifier.fillMaxWidth(),
                    faceNames = faceNames,
                    onNameConfirmed = { faceName ->
                        onNameConfirmed.invoke(true, currentResult.faceFileName, faceName)
                        unansweredCount--
                    }
                )
            }

            Line(modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
            )

            Button(
                onClick = { onDismiss.invoke() },
                modifier = Modifier
                    .align(Alignment.End)
                    .fillMaxWidth()
            ) {
                Text(text = "Done")
            }
        }
    }
}

@Composable
private fun FaceRecognised(
    suggestedName: String,
    modifier: Modifier = Modifier,
    onSuggestionAccepted: (isAccepted: Boolean) -> Unit,
) {

    Column(modifier = modifier) {
        Text(
            text = "Is this person $suggestedName?",
            modifier = Modifier.padding(8.dp)
        )
        Row(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { onSuggestionAccepted.invoke(true) },
                border = BorderStroke(width = 2.dp, color = Color.Green)
            ) { Text(text = "Yes") }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { onSuggestionAccepted.invoke(false) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
            ) { Text(text = "No") }
        }
    }
}

@Composable
private fun FaceNotRecognised(
    modifier: Modifier = Modifier,
    faceNames: List<String>,
    onNameConfirmed: (faceName: String) -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()

    var currentName by remember { mutableStateOf("") }

    var filteredSuggestions by remember { mutableStateOf(emptyList<String>()) }

    var showSuggestions by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = currentName,
                onValueChange = {
                    currentName = it
                    coroutineScope.launch(Dispatchers.IO) {
                        filteredSuggestions = faceNames
                            .filter { suggestion -> suggestion.startsWith(it) }
                    }
                }
            )

            DropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = showSuggestions,
                onDismissRequest = { showSuggestions = false }
            ) {
                filteredSuggestions.forEach { suggestion ->
                    DropdownMenuItem(onClick = {
                        currentName = suggestion
                        showSuggestions = false
                    }) {
                        Text(text = suggestion)
                    }
                }
            }
        }

        IconButton(onClick = { onNameConfirmed.invoke(currentName) }) {
            Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Confirm")
        }
    }
}