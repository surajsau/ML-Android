package `in`.surajsau.jisho.ui.styletransfer

import `in`.surajsau.jisho.ui.base.camera.Camera
import `in`.surajsau.jisho.ui.base.camera.CameraAction
import `in`.surajsau.jisho.ui.base.camera.CameraControls
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
internal fun CameraScreen(
    modifier: Modifier = Modifier,
    onImageCaptured: (String) -> Unit,
) {

    var cameraAction by remember { mutableStateOf(CameraAction.None) }

    Column(modifier = modifier) {

        Camera(
            modifier = Modifier.fillMaxWidth()
                .weight(1f),
            cameraAction = cameraAction,
            onImageCaptured = onImageCaptured
        )

        CameraControls(
            modifier = Modifier.fillMaxWidth()
                .weight(1f),
            onCameraAction = { cameraAction = it  }
        )
    }
}