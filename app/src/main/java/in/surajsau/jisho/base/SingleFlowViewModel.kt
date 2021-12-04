package `in`.surajsau.jisho.base

import kotlinx.coroutines.flow.StateFlow

interface SingleFlowViewModel<Event, State> {
    val state: StateFlow<State>
    fun onEvent(event: Event)
}