package `in`.surajsau.jisho.ui.cardreader

import `in`.surajsau.jisho.base.use
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
) {

    val (state, event) = use(
        viewModel = LocalOnboardingViewModel.current,
        initialStateValue = CardReaderViewModel.State()
    )

    Box(modifier = modifier) {
        when (val screen = state.screen) {
            is CardReaderViewModel.Screen.Intro -> IntroScreen(
                modifier = Modifier.fillMaxSize(),
                onScreenSelected = { event(CardReaderViewModel.Event.ScreenSelected(it)) }
            )

            is CardReaderViewModel.Screen.CardReader -> CardReaderScreen(modifier = Modifier.fillMaxSize())

            is CardReaderViewModel.Screen.EmptyDetails,

            is CardReaderViewModel.Screen.FilledDetails -> {
                DetailsScreen(
                    modifier = Modifier.fillMaxSize(),
                    screen = screen,
                    navigateBack = {}
                )
            }
        }
    }
}

@Composable
private fun IntroScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (CardReaderViewModel.Screen) -> Unit,
) {

    Box(modifier = modifier) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Welcome to Alumni App",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onScreenSelected.invoke(CardReaderViewModel.Screen.CardReader) }) {
                Text(text = "Open Card Reader")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "- or -")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onScreenSelected.invoke(CardReaderViewModel.Screen.EmptyDetails) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = MaterialTheme.colors.primary,
                    disabledBackgroundColor = Color.Transparent
                ),
                elevation = null,
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.primary),
                shape = MaterialTheme.shapes.small
            ) {
                Text(text = "Fill out manually")
            }

        }
    }
}