package `in`.surajsau.jisho.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
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
fun<Event, State> use(
    viewModel: SingleFlowViewModel<Event, State>,
    initialStateValue: State,
): ViewModelComponents<Event, State> {

    val lifecycleOwner = LocalLifecycleOwner.current

    val lifecycleAwareState = remember(viewModel.state, lifecycleOwner) {
        viewModel.state.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }

    val state = lifecycleAwareState.collectAsState(initialStateValue)

    return ViewModelComponents(
        state = state.value,
        onEvent = { viewModel.onEvent(it) }
    )
}