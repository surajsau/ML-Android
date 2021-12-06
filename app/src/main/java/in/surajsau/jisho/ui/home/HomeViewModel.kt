package `in`.surajsau.jisho.ui.home

import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.data.ScreensDataProvider
import `in`.surajsau.jisho.data.model.Screen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    private val screensDataProvider: ScreensDataProvider,
) : ViewModel(), HomeViewModel {

    private val _screens = screensDataProvider.fetchScreen()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    override val state: StateFlow<HomeViewModel.State>
        get() = _screens
            .map { HomeViewModel.State(screens = it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, HomeViewModel.State())

    override fun onEvent(event: HomeViewModel.Event) {}

}

interface HomeViewModel: SingleFlowViewModel<HomeViewModel.Event, HomeViewModel.State> {

    sealed class Event

    data class State(
        val screens: List<Screen> = emptyList(),
    )

}

val LocalHomeViewModel = compositionLocalOf<HomeViewModel> {
    error("HomeViewModel not provided")
}