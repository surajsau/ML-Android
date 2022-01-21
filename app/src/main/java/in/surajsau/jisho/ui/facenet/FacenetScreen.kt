package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.base.FileName
import `in`.surajsau.jisho.base.use
import `in`.surajsau.jisho.ui.base.forEachRow
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import java.io.File

@Composable
fun FacenetScreen(modifier: Modifier = Modifier) {

    val (state, event) = use(viewModel = LocalFacenetViewModel.current)

    Box(modifier = modifier) {
        when (state.screenMode) {
            FacenetViewModel.ScreenMode.AddFace -> AddNewFaceScreen(
                modifier = Modifier.fillMaxSize(),
                onImageCaptured = { event(FacenetViewModel.Event.CameraResultReceived(it)) },
                onPermissionDismissed = { event(FacenetViewModel.Event.CameraPermissionDenied) }
            )

            FacenetViewModel.ScreenMode.RecogniseFace -> {

            }

            FacenetViewModel.ScreenMode.Gallery -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        itemsIndexed(state.personImages) { index, face ->
                            Box(modifier = Modifier
                                .padding(
                                    start = if (index == 0) 116.dp else 0.dp,
                                    end = 16.dp
                                )
                                .clickable { event(FacenetViewModel.Event.FaceSelected(faceName = face.faceName)) }
                            ) {
                                SavedFaceImage(
                                    faceName = face.faceName,
                                    filePath = face.imageFilePath,
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(state.images.chunked(3)) { images ->
                            images.forEachRow(modifier = Modifier.height(200.dp)) {
                                Image(
                                    painter = rememberImagePainter(data = File(it.imageFilePath)),
                                    contentDescription = it.faceName,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(2.dp)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { event(FacenetViewModel.Event.AddNewFaceClicked) },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(24.dp)
                    ) {
                        Text(text = "Add a new Face")
                    }
                }
            }

            FacenetViewModel.ScreenMode.Empty -> {
                Button(
                    onClick = { event(FacenetViewModel.Event.AddNewFaceClicked) },
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

    when (state.imageDialogMode) {
        is FacenetViewModel.ImageDialogMode.ShowAddFace -> {
            AddFaceDialog(
                filePath = state.imageDialogMode.faceFilePath,
                onNameAdded = { event(FacenetViewModel.Event.FaceNameReceived(
                    faceFileName = state.imageDialogMode.faceFileName,
                    imageFileName = state.imageDialogMode.imageFileName,
                    faceName = it
                )) },
                onDismiss = { event(FacenetViewModel.Event.DismissImageDialog) }
            )
        }

        else -> { /* do nothing */}
    }
}