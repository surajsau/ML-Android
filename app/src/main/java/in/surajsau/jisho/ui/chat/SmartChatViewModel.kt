package `in`.surajsau.jisho.ui.chat

import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.domain.models.User
import `in`.surajsau.jisho.domain.models.chat.ChatRowModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class SmartChatViewModelImpl: ViewModel(), SmartChatViewModel {

    override val state: StateFlow<SmartChatViewModel.State>
        get() = TODO("Not yet implemented")

    override fun onEvent(event: SmartChatViewModel.Event) {
        when (event) {

        }
    }

}

interface SmartChatViewModel : SingleFlowViewModel<SmartChatViewModel.Event, SmartChatViewModel.State> {

    sealed class Event {
        data class SendMessage(val message: String): Event()
    }

    data class State(
        val messages: List<ChatRowModel>,
        val user: User,
    )
}