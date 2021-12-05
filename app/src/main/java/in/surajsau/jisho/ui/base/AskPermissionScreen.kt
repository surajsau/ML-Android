package `in`.surajsau.jisho.ui.base

import `in`.surajsau.jisho.ui.theme.Purple500
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskPermissionScreen(
    modifier: Modifier = Modifier,
    permission: String,
    onDismiss: () -> Unit,
    onPermissionDeniedFallback: (() -> Unit)? = null,
    onPermissionAcceptedContents: @Composable () -> Unit,
) {

    val permissionState = rememberPermissionState(permission = permission)

    var doNotShowRationale by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        when {
            permissionState.hasPermission -> onPermissionAcceptedContents.invoke()

            permissionState.shouldShowRationale
                    || !permissionState.permissionRequested -> {

                if (doNotShowRationale) {

                    PermissionRationale(
                        title = "Check settings",
                        onDismissed = { onDismiss.invoke() },
                        onAccepted = { onPermissionDeniedFallback?.invoke() }
                    )

                } else {
                    PermissionRationale(
                        title = "Require Camera permission",
                        onDismissed = { doNotShowRationale = true },
                        onAccepted = { permissionState.launchPermissionRequest() }
                    )
                }
            }

            else -> {

                PermissionRationale(
                    title = "Check settings",
                    onDismissed = { onDismiss.invoke() },
                    onAccepted = { onPermissionDeniedFallback?.invoke() }
                )
            }

        }
    }


}

@Composable
fun PermissionRationale(
    modifier: Modifier = Modifier,
    title: String,
    onDismissed: () -> Unit,
    onAccepted: () -> Unit
) {

    Column(modifier = modifier
        .fillMaxSize()
        .background(Color.White)) {

        Text(
            text = title,
            color = Color.DarkGray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onDismissed.invoke() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = Purple500
                )
            ) {
                Text(text = "Cancel",
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Button(
                onClick = { onAccepted.invoke() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = Purple500
                )
            ) {
                Text(text = "Cancel",
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

    }
}

