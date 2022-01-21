package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.base.ObserveLifecycle
import `in`.surajsau.jisho.base.use
import `in`.surajsau.jisho.domain.models.GalleryModel
import `in`.surajsau.jisho.ui.base.AskPermissionScreen
import `in`.surajsau.jisho.ui.base.CameraScreen
import `in`.surajsau.jisho.ui.base.ForEachRow
import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import coil.compose.rememberImagePainter
import java.io.File

@Composable
fun FacenetScreen(modifier: Modifier = Modifier) {

    val (state, event) = use(viewModel = LocalFacenetViewModel.current, FacenetViewModel.State())

    ObserveLifecycle {
        when (it) {
            Lifecycle.Event.ON_STOP -> event(FacenetViewModel.Event.Close)
            Lifecycle.Event.ON_START -> event(FacenetViewModel.Event.Initiate)

            else -> { /* do nothing */ }
        }
    }

    Box(modifier = modifier) {
        when (state.screenMode) {
            FacenetViewModel.ScreenMode.AddFace,
            FacenetViewModel.ScreenMode.RecogniseFace -> {
                AskPermissionScreen(
                    modifier = modifier,
                    permission = Manifest.permission.CAMERA,
                    onDismiss = { /* do nothing */ }
                ) {
                    CameraScreen(
                        modifier = Modifier.fillMaxSize(),
                        onImageCaptured = {
                            event(FacenetViewModel.Event.CameraResultReceived(fileName = it))
                        }
                    )
                }
            }

            FacenetViewModel.ScreenMode.Gallery -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Faces
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        itemsIndexed(state.personImages) { index, face ->
                            Box(modifier = Modifier
                                .padding(
                                    start = if (index == 0) 8.dp else 0.dp,
                                    end = 8.dp
                                )
                                .clickable { event(FacenetViewModel.Event.FaceSelected(faceName = face.faceName)) }
                            ) {
                                SavedFace(
                                    modifier = Modifier.size(56.dp),
                                    model = face
                                )
                            }
                        }
                    }

                    // Images
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        items(state.images.chunked(FacenetViewModel.GalleryModelsPerRow)) { images ->
                            ForEachRow(
                                items = images,
                                modifier = Modifier.height(200.dp),
                            ) {
                                when (it) {
                                    is GalleryModel.Image -> {
                                        Image(
                                            painter = rememberImagePainter(data = File(it.imageFilePath)),
                                            contentDescription = it.faceName,
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .padding(2.dp),
                                            contentScale = ContentScale.FillWidth
                                        )
                                    }

                                    is GalleryModel.Empty -> { Spacer(modifier = Modifier.weight(1f)) }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { event(FacenetViewModel.Event.OpenCameraClicked) },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.End)
                            .size(48.dp),
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    }
                }
            }

            FacenetViewModel.ScreenMode.Empty -> {
                Button(
                    onClick = { event(FacenetViewModel.Event.OpenCameraClicked) },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(text = "Add a new Face")
                }
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

    when (val dialog = state.checkFaceDialog) {
        is FacenetViewModel.CheckFaceDialog.Show -> {
            CheckFaceDialog(
                results = dialog.recognitionResults,
                onNameConfirmed = { isNewFace, faceFileName, name -> event(FacenetViewModel.Event.FaceConfirmed(
                    imageFileName = dialog.imageFileName,
                    faceFileName = faceFileName,
                    faceName = name,
                    isNewFace = isNewFace,
                )) },
                onDismiss = { event(FacenetViewModel.Event.DismissCheckFaceDialog) }
            )
        }

        else -> { /* do nothing */}
    }
}