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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
                    modifier = Modifier
                        .size(48.dp)
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
                model = state.messageContainerModel,
                modifier = Modifier.fillMaxWidth(),
                onMessageTextChanged = { event(SmartChatViewModel.Event.MessageTextChanged(it)) },
                onSendMessageClicked = { event(SmartChatViewModel.Event.SendMessageClicked) }
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

    LazyColumn(
        modifier = modifier
            .background(Color.LightGray)
            .padding(horizontal = 16.dp),
        reverseLayout = true,
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
    model: SmartChatViewModel.MessageContainerModel,
    modifier: Modifier = Modifier,
    onMessageTextChanged: (String) -> Unit,
    onSendMessageClicked: () -> Unit,
) {

    Column(modifier = modifier) {

        Text(
            text = when(model.currentUser) {
                SmartChatViewModel.CurrentUser.LOCAL -> "Message as Me"
                SmartChatViewModel.CurrentUser.REMOTE -> "Message as Other"
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = when (model.currentUser) {
                        SmartChatViewModel.CurrentUser.LOCAL -> MaterialTheme.colors.primary
                        SmartChatViewModel.CurrentUser.REMOTE -> MaterialTheme.colors.secondary
                    }
                )
                .padding(vertical = 2.dp),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = model.text,
                onValueChange = { onMessageTextChanged.invoke(it) },
                shape = RoundedCornerShape(size = 8.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { onSendMessageClicked.invoke() },
                modifier = Modifier
                    .alpha(alpha = if (model.isSendButtonEnabled) 1f else 0.5f)
                    .background(color = MaterialTheme.colors.primary, shape = CircleShape),
                enabled = model.isSendButtonEnabled
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Send message",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Preview
@Composable
private fun previewSendMessageContainer() {

    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)) {
        SendMessageContainer(
            model = SmartChatViewModel.MessageContainerModel(
                currentUser = SmartChatViewModel.CurrentUser.REMOTE,
                isSendButtonEnabled = false,
            ),
            onMessageTextChanged = {},
            onSendMessageClicked = {}
        )
    }
}