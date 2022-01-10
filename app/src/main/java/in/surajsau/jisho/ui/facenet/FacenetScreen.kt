package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.base.FileName
import `in`.surajsau.jisho.base.use
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                                    start = if (index == 0) 8.dp else 0.dp,
                                    end = 8.dp
                                )
                                .clickable { event(FacenetViewModel.Event.FaceSelected(faceName = face.faceName)) }
                            ) {
                                SavedFaceImage(
                                    faceName = face.faceName,
                                    fileName = FileName(face.fileName),
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { event(FacenetViewModel.Event.AddNewFaceClicked) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
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