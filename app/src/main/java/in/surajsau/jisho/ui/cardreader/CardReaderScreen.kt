package `in`.surajsau.jisho.ui.cardreader

import `in`.surajsau.jisho.base.use
import `in`.surajsau.jisho.ui.base.AskPermissionScreen
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardReaderScreen(
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
) {

    val context = LocalContext.current

    val (state, event) = use(viewModel = LocalOnboardingViewModel.current, initialStateValue = CardReaderViewModel.State())

    LaunchedEffect(state.instruction) {
        val instruction = state.instruction.takeIf { it.isNotEmpty() } ?: return@LaunchedEffect
        Toast.makeText(context, instruction, Toast.LENGTH_SHORT).show()
    }

    Box(modifier = modifier) {
        AskPermissionScreen(
            permission = android.Manifest.permission.CAMERA,
            onDismiss = { onDismiss?.invoke() },
            onPermissionDeniedFallback = { event(CardReaderViewModel.Event.OnPermissionDenied) }
        ) {
            CameraScreen(
                modifier = Modifier.fillMaxSize(),
                instructionMessage = state.instruction,
                onImageCaptured = { event(CardReaderViewModel.Event.CameraResultReceived(it)) }
            )
        }

        if (state.showLoader) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 24.dp)
            )
        }
    }
}