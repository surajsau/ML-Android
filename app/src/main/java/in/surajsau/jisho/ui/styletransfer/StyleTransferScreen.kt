package `in`.surajsau.jisho.ui.styletransfer

import `in`.surajsau.jisho.base.use
import `in`.surajsau.jisho.ui.base.AskPermissionScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun StyleTransferScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToSettings: () -> Unit,
) {

    val (state, event) = use(LocalStyleTransferViewModel.current, StyleTransferViewModel.State())

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        val lifecycleObserver = LifecycleEventObserver { _, lifecycleEvent ->
            if (lifecycleEvent == Lifecycle.Event.ON_STOP) {
                event(StyleTransferViewModel.Event.OnStop)
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    Box(modifier = modifier
        .fillMaxSize()
        .background(Color.White)
    ) {

        AskPermissionScreen(
            modifier = Modifier.fillMaxSize(),
            permission = android.Manifest.permission.CAMERA,
            onDismiss = { navigateBack.invoke() },
            onPermissionDeniedFallback = { navigateToSettings.invoke() },
        ) {

            when (val screenMode = state.mode) {
                is StyleTransferViewModel.ScreenMode.Camera -> {
                    CameraScreen(modifier = Modifier.fillMaxSize(),
                        onImageCaptured = { fileName ->
                            event(StyleTransferViewModel.Event.CameraResultReceived(fileName))
                        }
                    )
                }

                is StyleTransferViewModel.ScreenMode.StylePreview -> {
                    StylePreviewScreen(
                        modifier = Modifier.fillMaxSize(),
                        imagePreview = {
                            ImagePreview(
                                bitmap = screenMode.image,
                                showLoader = screenMode.showLoading,
                                modifier = Modifier.fillMaxSize()
                            )
                        },
                        previewGallery = {
                            PreviewGallery(
                                previews = screenMode.stylePreviews,
                                onStyleSelected = { fileName ->
                                    event(StyleTransferViewModel.Event.StyleSelected(fileName))
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    )
                }
            }

        }
    }

}