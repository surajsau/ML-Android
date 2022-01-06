package `in`.surajsau.jisho.ui.gpt

import `in`.surajsau.jisho.data.model.ChatMessage
import `in`.surajsau.jisho.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MessageRow(
    isMyMessage: Boolean,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(modifier = modifier, horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start) {
        Box(modifier = Modifier.widthIn(max = 200.dp)) {
            content()
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage.Message,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        Box(modifier = Modifier
            .background(
                if (message.isMe) colorResource(id = R.color.purple_500) else colorResource(id = R.color.teal_700),
                RoundedCornerShape(16.dp)
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
            .background(Color.DarkGray, RoundedCornerShape(16.dp))
    )
}