package `in`.surajsau.jisho.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.StateFlow

interface SingleFlowViewModel<Event, State> {
    val state: StateFlow<State>
    fun onEvent(event: Event)
}

data class ViewModelComponents<Event, State>(
    val state: State,
    val onEvent: (Event) -> Unit
)

@Composable
fun<Event, State> use(viewModel: SingleFlowViewModel<Event, State>): ViewModelComponents<Event, State> {
    val state by viewModel.state.collectAsState()
    return ViewModelComponents(
        state = state,
        onEvent = { viewModel.onEvent(it) }
    )
}