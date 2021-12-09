package `in`.surajsau.jisho.ui.styletransfer

import `in`.surajsau.jisho.R
import android.view.Surface.ROTATION_270
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

sealed class CameraAction {
    object Click: CameraAction()
    object SwitchCamera: CameraAction()
}

private fun CameraSelector.toggle() = when (this) {
    CameraSelector.DEFAULT_BACK_CAMERA -> CameraSelector.DEFAULT_FRONT_CAMERA
    else -> CameraSelector.DEFAULT_BACK_CAMERA
}

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    onImageCaptured: (String) -> Unit,
) {

    val context = LocalContext.current

    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }

    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }

    Column(modifier = modifier) {

        Camera(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            imageCapture = imageCapture,
            cameraSelector = cameraSelector
        )

        CameraControls(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onCameraAction = {
                when (it) {
                    is CameraAction.Click -> {
                        val imageFileName = "${System.currentTimeMillis()}.jpg"
                        val imageFile = File(context.externalCacheDir, imageFileName)
                        val outputImageOption = ImageCapture.OutputFileOptions.Builder(imageFile)
                            .build()

                        imageCapture.takePicture(
                            outputImageOption,
                            ContextCompat.getMainExecutor(context),
                            object: ImageCapture.OnImageSavedCallback {
                                override fun onError(exception: ImageCaptureException) {
                                    exception.printStackTrace()
                                }

                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    onImageCaptured.invoke(imageFileName)
                                }
                            }
                        )
                    }
                    is CameraAction.SwitchCamera -> {
                        cameraSelector = cameraSelector.toggle()
                    }
                }
            }
        )
    }
}

@Composable
private fun Camera(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    val context = LocalContext.current

    val previewView = remember { PreviewView(context).apply { this.scaleType = scaleType } }

    LaunchedEffect(cameraSelector) {
        val cameraProvider = suspendCoroutine<ProcessCameraProvider> { continuation ->
            ProcessCameraProvider.getInstance(context).also { future ->
                future.addListener({
                    continuation.resume(future.get())
                }, ContextCompat.getMainExecutor(context))
            }
        }

        val previewUseCase = Preview.Builder()
            .build()
            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

        runCatching {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector,
                previewUseCase,
                imageCapture
            )
        }
    }

    AndroidView(modifier = modifier, factory = { previewView })

}

@Composable
private fun CameraControls(
    modifier: Modifier = Modifier,
    onCameraAction: (CameraAction) -> Unit
) {

    Row(
        modifier = modifier
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {

        Spacer(modifier = Modifier.size(48.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .border(width = 2.dp, color = Color.DarkGray, shape = CircleShape)
                .background(Color.White, CircleShape)
                .clickable { onCameraAction.invoke(CameraAction.Click) }
        )

        Image(
            painter = painterResource(id = R.drawable.ic_switch_camera),
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .clickable { onCameraAction.invoke(CameraAction.SwitchCamera) }
                .clipToBounds(),
            contentScale = ContentScale.Fit
        )

    }

}