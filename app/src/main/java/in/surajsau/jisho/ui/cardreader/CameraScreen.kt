package `in`.surajsau.jisho.ui.cardreader

import `in`.surajsau.jisho.ui.base.camera.Camera
import `in`.surajsau.jisho.ui.base.camera.CameraAction
import `in`.surajsau.jisho.ui.base.camera.CameraControls
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    instructionMessage: String,
    onImageCaptured: (String) -> Unit,
) {

    var cameraAction by remember { mutableStateOf(CameraAction.None) }

    Column(modifier = modifier) {

        Camera(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            cameraAction = cameraAction,
            onImageCaptured = {
                onImageCaptured.invoke(it)
                cameraAction = CameraAction.None
            })

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = instructionMessage,
        )

        CameraControls(
            modifier = Modifier.fillMaxWidth()
                .weight(1f),
            onCameraAction = { cameraAction = it }
        )
    }
}