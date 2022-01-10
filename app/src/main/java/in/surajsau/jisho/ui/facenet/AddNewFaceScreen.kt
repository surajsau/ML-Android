package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.ui.base.AskPermissionScreen
import `in`.surajsau.jisho.ui.base.CameraScreen
import android.Manifest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddNewFaceScreen(
    modifier: Modifier = Modifier,
    onImageCaptured: (String) -> Unit,
    onPermissionDismissed: () -> Unit,
) {

    AskPermissionScreen(
        modifier = modifier,
        permission = Manifest.permission.CAMERA,
        onDismiss = onPermissionDismissed
    ) {
        CameraScreen(
            modifier = Modifier.fillMaxSize(),
            onImageCaptured = onImageCaptured
        )
    }
}