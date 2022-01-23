package `in`.surajsau.jisho.ui.cardreader

import `in`.surajsau.jisho.base.use
import `in`.surajsau.jisho.domain.models.CardDetails
import `in`.surajsau.jisho.ui.base.AskPermissionScreen
import `in`.surajsau.jisho.ui.base.CameraScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardReaderScreen(
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
) {

    val (state, event) = use(viewModel = LocalCardReaderViewModel.current, initialStateValue = CardReaderViewModel.State())

    Box(modifier = modifier
            .fillMaxSize()
    ) {
        AskPermissionScreen(
            permission = android.Manifest.permission.CAMERA,
            onDismiss = { onDismiss?.invoke() },
            onPermissionDeniedFallback = { event(CardReaderViewModel.Event.OnPermissionDenied) }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                CameraScreen(
                    modifier = Modifier.fillMaxSize(),
                    onImageCaptured = { event(CardReaderViewModel.Event.CameraResultReceived(it)) }
                )

                CardOverlay(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.8f),
                    message = state.instruction
                )
            }
        }

        if (state.showLoader) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 24.dp)
            )
        }
    }

    if (state.cardDetailsDialogMode is CardReaderViewModel.CardDetailsDialogMode.Show) {
        CardDetailsDialog(
            cardDetails = state.cardDetailsDialogMode.cardDetails,
            modifier = Modifier.fillMaxWidth()
                .padding(24.dp)
        )
    }
}

@Composable
private fun CardOverlay(
    modifier: Modifier = Modifier,
    message: String,
) {

    Column(modifier = modifier) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .background(Color.DarkGray)
        ) {

            Text(
                text = message,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
        )

        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .background(Color.DarkGray)
            .alpha(0.2f)
        )
    }
}

@Composable
private fun CardDetailsDialog(
    modifier: Modifier = Modifier,
    cardDetails: CardDetails,
    onDismiss: (() -> Unit)? = null,
) {

    Dialog(
        onDismissRequest = { onDismiss?.invoke() }
    ) {
        Column(
            modifier = modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(text = cardDetails.cardNumber)
            Text(text = cardDetails.expiry)
            Text(text = cardDetails.cardHolderName)
        }
    }
}