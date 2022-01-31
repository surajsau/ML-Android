package `in`.surajsau.jisho.ui.chat

import `in`.surajsau.jisho.domain.models.chat.ChatRowModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

@Composable
fun ChatRow(
    chatRowModel: ChatRowModel,
    modifier: Modifier = Modifier
) {

    Log.e("SmartChat", "$chatRowModel")

    Box(modifier = modifier) {
        when (chatRowModel) {
            is ChatRowModel.Message ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    MessageBubble(
                        chatRowModel = chatRowModel,
                        modifier = Modifier
                            .padding(
                                start = if (chatRowModel.isLocal) 16.dp else 0.dp,
                                end = if (chatRowModel.isLocal) 0.dp else 16.dp
                            )
                            .align(
                                alignment = if (chatRowModel.isLocal) Alignment.End else Alignment.Start
                            )
                    )
                }

            is ChatRowModel.PictureMessage ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    PictureMessageBubble(
                        chatRowModel = chatRowModel,
                        modifier = Modifier
                            .padding(
                                start = if (chatRowModel.isLocal) 16.dp else 0.dp,
                                end = if (chatRowModel.isLocal) 0.dp else 16.dp
                            )
                            .align(
                                alignment = if (chatRowModel.isLocal) Alignment.End else Alignment.Start
                            )
                    )
                }

            is ChatRowModel.Typing -> TypingBubble()
        }
    }
}

@Composable
private fun TypingBubble(
    modifier: Modifier = Modifier
) {

    Row(modifier = modifier
        .background(
            color = MaterialTheme.colors.secondary,
            shape = RoundedCornerShape(size = 8.dp)
        )
        .padding(vertical = 8.dp, horizontal = 16.dp),
    ) {

        Box(modifier = Modifier
            .size(8.dp)
            .background(color = Color.White, shape = CircleShape)
            .alpha(0.5f)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Box(modifier = Modifier
            .size(8.dp)
            .background(color = Color.White, shape = CircleShape)
            .alpha(0.5f)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Box(modifier = Modifier
            .size(8.dp)
            .background(color = Color.White, shape = CircleShape)
            .alpha(0.5f)
        )
    }
}

@Composable
private fun MessageBubble(
    chatRowModel: ChatRowModel.Message,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier
        .background(
            color = if (chatRowModel.isLocal) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
            shape = RoundedCornerShape(size = 8.dp)
        )
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        ChatClickableText(
            annotatedString = chatRowModel.value,
            annotationMaps = chatRowModel.annotationMaps,
        )

        Text(
            text = chatRowModel.timestamp,
            modifier = Modifier
                .alpha(0.5f)
                .padding(top = 4.dp)
                .align(Alignment.End),
            color = Color.White,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun PictureMessageBubble(
    chatRowModel: ChatRowModel.PictureMessage,
    modifier: Modifier = Modifier,
) {

    val painter = rememberImagePainter(data = chatRowModel.imageUrl)

    Column(modifier = modifier
        .background(
            color = if (chatRowModel.isLocal) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
            shape = RoundedCornerShape(size = 8.dp)
        )
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        Image(
            painter = painter,
            contentDescription = "",
            modifier = Modifier.fillMaxWidth()
                .aspectRatio(ratio = 1f),
            contentScale = ContentScale.Crop
        )

        ChatClickableText(
            annotatedString = chatRowModel.message,
            annotationMaps = chatRowModel.annotationMaps,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = chatRowModel.timestamp,
            modifier = Modifier
                .alpha(0.5f)
                .padding(top = 4.dp)
                .align(Alignment.End),
            color = Color.White,
            fontSize = 12.sp,
        )
    }
}

@Preview
@Composable
private fun PreviewChatRow() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {

        ChatRow(
            chatRowModel = ChatRowModel.Message(
                isLocal = true,
                value = AnnotatedString("Sample message from Local User"),
                timestamp = "09:30",
                annotationMaps = emptyList()
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        ChatRow(
            chatRowModel = ChatRowModel.Message(
                isLocal = false,
                value = AnnotatedString("Sample message from Remote User"),
                timestamp = "09:30",
                annotationMaps = emptyList()
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        ChatRow(
            chatRowModel = ChatRowModel.Message(
                isLocal = false,
                value = buildAnnotatedString {
                    append("Sample message from Remote User. Sample message from Remote User. Sample ")

                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("message from Remote")
                    }

                    append(" User. Sample message from Remote User")
                },
                timestamp = "09:30",
                annotationMaps = emptyList()
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        ChatRow(
            chatRowModel = ChatRowModel.Typing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}