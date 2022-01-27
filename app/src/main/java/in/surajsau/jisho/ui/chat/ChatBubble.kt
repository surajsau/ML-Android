package `in`.surajsau.jisho.ui.chat

import `in`.surajsau.jisho.domain.models.chat.ChatRowModel
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatRow(
    chatRowModel: ChatRowModel,
    modifier: Modifier = Modifier
) {

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
        .padding(16.dp)
    ) {
        Text(
            text = chatRowModel.value,
            color = Color.White,
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
                value = "Sample message from Local User",
                timestamp = "09:30"
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        ChatRow(
            chatRowModel = ChatRowModel.Message(
                isLocal = false,
                value = "Sample message from Remote User",
                timestamp = "09:30"
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        ChatRow(
            chatRowModel = ChatRowModel.Message(
                isLocal = false,
                value = "Sample message from Remote User. Sample message from Remote User. Sample message from Remote User. Sample message from Remote User",
                timestamp = "09:30"
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