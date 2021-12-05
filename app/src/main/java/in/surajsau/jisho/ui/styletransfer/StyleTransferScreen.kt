package `in`.surajsau.jisho.ui.styletransfer

import `in`.surajsau.jisho.base.use
import `in`.surajsau.jisho.ui.base.AskPermissionScreen
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun StyleTransferScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToSettings: () -> Unit,
) {

    val (state, event) = use(LocalStyleTransferViewModel.current)

    Box(modifier = modifier.fillMaxSize()) {

        AskPermissionScreen(
            modifier = Modifier.fillMaxSize(),
            permission = android.Manifest.permission.CAMERA,
            onDismiss = { navigateBack.invoke() },
            onPermissionDeniedFallback = { navigateToSettings.invoke() },
        ) {
            Camera(modifier = Modifier.fillMaxSize())
        }
    }

}