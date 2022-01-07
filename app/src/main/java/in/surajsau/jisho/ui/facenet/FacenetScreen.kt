package `in`.surajsau.jisho.ui.facenet

import `in`.surajsau.jisho.base.FileName
import `in`.surajsau.jisho.base.use
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
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
                }
            }

            FacenetViewModel.ScreenMode.Empty -> {
                Button(onClick = { event(FacenetViewModel.Event.AddNewFaceClicked) }) {
                    Text(text = "Add a new Face")
                }
            }
        }
    }

    when (state.imageDialogMode) {
        is FacenetViewModel.ImageDialogMode.ShowAddFace -> {
            AddFaceDialog(
                filePath = state.imageDialogMode.filePath,
                face = state.imageDialogMode.face,
                onNameAdded = { event(FacenetViewModel.Event.FaceNameReceived(fileName = state.imageDialogMode.fileName, faceName = it)) },
                onDismiss = { event(FacenetViewModel.Event.DismissImageDialog) }
            )
        }

        else -> { /* do nothing */}
    }
}