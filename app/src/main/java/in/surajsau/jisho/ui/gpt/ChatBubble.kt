package `in`.surajsau.jisho.ui.gpt

import `in`.surajsau.jisho.data.model.ChatMessage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MessageRow(
    isMyMessage: Boolean,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(modifier = modifier) {
        if (isMyMessage) {
            Spacer(modifier = Modifier.weight(1f))
            content()
        } else {
            content()
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage.Message,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier.width(300.dp)) {
        Box(modifier = Modifier.fillMaxWidth()
            .background(
                if (message.isMe) Color.DarkGray else Color.LightGray,
                RoundedCornerShape(4.dp)
            )
        ) {

            Text(
                text = message.text,
                modifier = Modifier.padding(8.dp),
                color = Color.White,
            )
        }

        Text(
            text = "${message.timeStamp}",
            fontSize = 12.sp,
            color = Color.LightGray,
            modifier = Modifier.align(if (message.isMe) Alignment.End else Alignment.Start)
        )
    }
}


@Composable
fun TypingBubble(modifier: Modifier = Modifier) {

    Box(
        modifier = modifier
            .size(width = 300.dp, height = 50.dp)
            .background(Color.DarkGray, RoundedCornerShape(4.dp))
    )
}