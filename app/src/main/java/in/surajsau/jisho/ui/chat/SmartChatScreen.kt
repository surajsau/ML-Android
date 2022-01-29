package `in`.surajsau.jisho.ui.chat

import `in`.surajsau.jisho.base.Optional
import `in`.surajsau.jisho.base.use
import `in`.surajsau.jisho.domain.models.chat.ChatDetails
import `in`.surajsau.jisho.domain.models.chat.ChatRowModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

@Composable
fun SmartChatScreen(
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
) {

    val (state, event) = use(
        viewModel = LocalSmartChatViewModel.current,
        initialStateValue = SmartChatViewModel.State()
    )

    Column(modifier = modifier) {

        if (state.chatDetails is Optional.Some) {
            Toolbar(
                chatDetails = state.chatDetails.data,
                onBackClicked = { onDismiss?.invoke() }
            )
        }

        if (state.showLoader) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                        .align(Alignment.Center)
                )
            }

        } else {
            ChatBody(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                messages = state.messages
            )

            SendMessageContainer(
                modifier = Modifier
                    .padding(horizontal = 16.dp,)
                    .fillMaxWidth(),
                onSendClicked = { event(SmartChatViewModel.Event.SendMessage(it)) },
            )
        }
    }
}

@Composable
private fun Toolbar(
    chatDetails: ChatDetails,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {

    TopAppBar(
        modifier = modifier,
        backgroundColor = Color.White,
    ) {
        IconButton(onClick = { onBackClicked.invoke() }) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
        }

        Spacer(modifier = Modifier.width(8.dp))

        Image(
            painter = rememberImagePainter(data = chatDetails.chatIconUrl),
            contentDescription = "",
            modifier = Modifier
                .clip(CircleShape)
                .size(48.dp)
                .clipToBounds()
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = chatDetails.chatName,
            modifier = Modifier.weight(1f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ChatBody(
    modifier: Modifier = Modifier,
    messages: List<ChatRowModel>
) {

    LazyColumn(modifier = modifier
        .background(Color.LightGray)
        .padding(horizontal = 16.dp)
    ) {
        itemsIndexed(messages) { index, chatRow ->
            ChatRow(
                chatRowModel = chatRow,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = if (index == 0) 8.dp else 0.dp,
                        bottom = 8.dp
                    )
            )
        }
    }
}

@Composable
private fun SendMessageContainer(
    modifier: Modifier = Modifier,
    onSendClicked: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .background(Color.White)
            .padding(vertical = 8.dp)
    ) {
        TextField(
            value = text,
            onValueChange = { text = it }
        )

        IconButton(
            onClick = { onSendClicked.invoke(text) },
            modifier = Modifier
                .size(36.dp)
                .background(color = MaterialTheme.colors.primary)
        ) {
            Icon(imageVector = Icons.Filled.Send, contentDescription = "")
        }
    }
}