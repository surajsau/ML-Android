package `in`.surajsau.jisho.ui.styletransfer

import `in`.surajsau.jisho.base.LocalBitmapCache
import `in`.surajsau.jisho.base.use
import `in`.surajsau.jisho.ui.base.AskPermissionScreen
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun StyleTransferScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToSettings: () -> Unit,
) {

    val (state, event) = use(LocalStyleTransferViewModel.current)

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

    Box(modifier = modifier.fillMaxSize()) {

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
                            ImagePreview(bitmap = screenMode.image, showLoader = screenMode.showLoading)
                        },
                        previewGallery = {
                            PreviewGallery(
                                previews = screenMode.stylePreviews,
                                onStyleSelected = { fileName ->
                                    event(StyleTransferViewModel.Event.StyleSelected(fileName))
                                }
                            )
                        }
                    )
                }
            }

        }
    }

}

@Composable
fun StylePreviewScreen(
    imagePreview: @Composable () -> Unit,
    previewGallery: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(modifier = modifier.background(Color.Black)) {

        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
            imagePreview()
        }

        previewGallery()
    }

}

@Composable
fun ImagePreview(
    bitmap: Bitmap?,
    modifier: Modifier = Modifier,
    showLoader: Boolean = false,
) {

    Box(modifier = modifier) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (showLoader) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun PreviewGallery(
    previews: List<String>,
    modifier: Modifier = Modifier,
    onStyleSelected: (String) -> Unit
) {

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        itemsIndexed(previews) { index, fileName ->
            PreviewGalleryItem(
                fileName = fileName,
                onClick = { onStyleSelected.invoke(it) },
                modifier = Modifier
                    .width(96.dp)
                    .height(96.dp)
                    .padding(
                        start = if (index == 0) 16.dp else 0.dp,
                        end = 16.dp
                    )
            )
        }
    }
}

@Composable
fun PreviewGalleryItem(
    fileName: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val bitmapCache = LocalBitmapCache.current

    val context = LocalContext.current

    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(fileName) {
        if (!bitmapCache.has(fileName)) {
            launch(Dispatchers.IO) {
                val bitmapStream = context.assets.open(fileName)
                val bitmap = BitmapFactory.decodeStream(bitmapStream)

                bitmapCache.save(fileName, bitmap)
                bitmapStream.close()

                previewBitmap = bitmap
            }
        } else {
            previewBitmap = bitmapCache.get(fileName)
        }
    }

    if (previewBitmap != null) {
        Image(
            bitmap = previewBitmap!!.asImageBitmap(),
            contentDescription = null,
            modifier = modifier
                .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .clipToBounds()
                .clickable { onClick.invoke(fileName) },
            contentScale = ContentScale.Crop
        )
    }
}