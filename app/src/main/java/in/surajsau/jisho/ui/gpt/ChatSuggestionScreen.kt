package `in`.surajsau.jisho.ui.gpt

import `in`.surajsau.jisho.R
import `in`.surajsau.jisho.base.use
import `in`.surajsau.jisho.data.model.ChatMessage
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

private val KanyeProfilePicUrl = "https://www.washingtonpost.com/wp-apps/imrs.php?src=https://arc-anglerfish-washpost-prod-washpost.s3.amazonaws.com/public/NNXSYLVFAY5GFBWN6AVBIOBWVI.jpg&w=300&h=237"

@Composable
fun ChatSuggestionScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
) {

    val (state, event) = use(viewModel = LocalChatSuggestionViewModel.current, ChatSuggestionViewModel.State())

    Column(modifier = modifier) {

        TopAppBar(backgroundColor = Color.White, elevation = 0.dp) {

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
                IconButton(onClick = { navigateBack.invoke() }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
                }

                Image(
                    painter = rememberImagePainter(data = KanyeProfilePicUrl),
                    contentDescription = "Profile picture",
                    modifier = Modifier.padding(end = 16.dp)
                        .size(36.dp)
                        .background(Color.DarkGray, CircleShape)
                        .clip(CircleShape)
                        .clipToBounds()
                        .border(width = 2.dp, color = Color.DarkGray)
                )

                Text(text = "Kanye", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            ChatBlock(
                messages = state.messages,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            )

            if (state.showLoader) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        if (state.showSuggestionBlock) {
            SuggestionBlock(
                suggestionText = state.textSuggestion,
                onSuggestionClicked = { event(ChatSuggestionViewModel.Event.AcceptSuggestion(it)) },
                modifier = Modifier.fillMaxWidth(),
                showProgress = state.showSuggestionLoader
            )
        }

        MessageBlock(
            text = state.currentMessage,
            onMessageTextChanged = { event(ChatSuggestionViewModel.Event.TextChange(it)) },
            onSendClicked = { event(ChatSuggestionViewModel.Event.SendMessage) },
            modifier = Modifier.fillMaxWidth(),
            isSendButtonEnabled = state.isSendButtonEnabled
        )
    }
}

@Composable
fun SuggestionBlock(
    suggestionText: String,
    onSuggestionClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    showProgress: Boolean = false,
) {

    Box(modifier = modifier.background(Color.LightGray)) {

        if (showProgress) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp)
                    .align(Alignment.Center),
                strokeWidth = 2.dp
            )
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 8.dp)
                    .background(Color.DarkGray, RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = suggestionText,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp)
                        .clickable { onSuggestionClicked.invoke(suggestionText) },
                    color = Color.White,
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MessageBlock(
    text: String,
    onMessageTextChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isSendButtonEnabled: Boolean = false,
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier.background(Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {

        TextField(
            value = text,
            onValueChange = { onMessageTextChanged.invoke(it) },
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp),
            placeholder = { Text(text = "Enter your message") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            trailingIcon = {
                IconButton(
                    onClick = {
                        onSendClicked.invoke()
                        keyboardController?.hide()
                    },
                    enabled = isSendButtonEnabled
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send, 
                        contentDescription = "Send message",
                        tint = colorResource(id = R.color.purple_500)
                    )
                }
            },
            shape = RoundedCornerShape(24.dp)
        )

    }
}

@Composable
fun ChatBlock(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>
) {

    LazyColumn(modifier, reverseLayout = true) {
        itemsIndexed(messages) { index, message ->
            MessageRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp,
                        bottom = if (index == 0) 8.dp else 0.dp
                    ),
                isMyMessage = (message as? ChatMessage.Message)?.isMe ?: false,
                content = {
                    when (message) {
                        is ChatMessage.Typing -> TypingBubble()
                        is ChatMessage.Message -> MessageBubble(message = message)
                    }
                }
            )
        }
    }
}