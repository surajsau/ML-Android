package `in`.surajsau.jisho.ui.base.camera

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class CameraAction {
    Click, SwitchCamera, None
}

private fun CameraSelector.toggle() = when (this) {
    CameraSelector.DEFAULT_BACK_CAMERA -> CameraSelector.DEFAULT_FRONT_CAMERA
    else -> CameraSelector.DEFAULT_BACK_CAMERA
}

@Composable
fun Camera(
    modifier: Modifier = Modifier,
    cameraAction: CameraAction,
    onImageCaptured: (String) -> Unit,
) {

    val context = LocalContext.current

    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }

    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }

    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context).apply { this.scaleType = PreviewView.ScaleType.FILL_CENTER }
    }

    DisposableEffect(cameraAction) {
        when (cameraAction) {
            CameraAction.Click -> {
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
            CameraAction.SwitchCamera -> {
                cameraSelector = cameraSelector.toggle()
            }
        }

        onDispose {}
    }

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
                lifecycleOwner,
                cameraSelector,
                previewUseCase,
                imageCapture
            )
        }
    }

    AndroidView(modifier = modifier, factory = { previewView })
}