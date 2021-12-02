package `in`.surajsau.jisho.ui.digitalink

import `in`.surajsau.jisho.ui.theme.DigitalInkColors
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DigitalInkScreen(
    viewModel: DrawScreenViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    val state by viewModel.state.collectAsState(DrawScreenViewModel.State())

    Box(modifier = modifier) {
        if (state.showModelStatusProgress) {
            ModelStatusProgress(
                statusText = state.modelStatusText,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        } else {

            Column(modifier = Modifier.fillMaxSize()) {

                Card(elevation = 4.dp) {
                    TextField(
                        value = state.finalText,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        textStyle = TextStyle(color = DigitalInkColors.Text, fontSize = 24.sp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            placeholderColor = DigitalInkColors.PredictionPlaceholder,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = DigitalInkColors.PredictionText
                        ),
                        placeholder = {
                            Text(
                                text = "Enter text(Japanese)",
                                fontSize = 24.sp,
                            )
                        },
                        shape = RoundedCornerShape(0.dp)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f, fill = true))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(DigitalInkColors.PredictionBackground)
                ) {
                    items(state.predictions) { prediction ->
                        Prediction(
                            text = prediction,
                            onClick = { viewModel.onPredictionSelected(it) }
                        )
                    }
                }

                DrawSpace(
                    reset = state.resetCanvas,
                    onDrawEvent = { viewModel.onEvent(DrawScreenViewModel.Event.Pointer(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true)
                )
            }
        }
    }
}

@Composable
fun ModelStatusProgress(
    statusText: String,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {

        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 24.dp)
        )

        Text(
            text = statusText,
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
    }
}

@Composable
fun Prediction(
    text: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(modifier = modifier) {
        Text(
            text = text,
            color = DigitalInkColors.PredictionText,
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { onClick.invoke(text) },
        )

        Box(modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(DigitalInkColors.PredictionDivider)
        )
    }
}