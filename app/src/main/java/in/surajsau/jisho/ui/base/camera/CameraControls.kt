package `in`.surajsau.jisho.ui.base.camera

import `in`.surajsau.jisho.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CameraControls(
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