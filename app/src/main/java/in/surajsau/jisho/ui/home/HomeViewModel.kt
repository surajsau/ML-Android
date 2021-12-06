package `in`.surajsau.jisho.ui.home

import `in`.surajsau.jisho.base.SingleFlowViewModel
import `in`.surajsau.jisho.data.model.Screen
import `in`.surajsau.jisho.domain.FetchScreens
import `in`.surajsau.jisho.ui.Destinations
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    private val fetchScreens: FetchScreens,
) : ViewModel(), HomeViewModel {

    private val _navigationDestination = MutableStateFlow<Destinations?>(null)

    private val _screens = fetchScreens.invoke()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    override val state: StateFlow<HomeViewModel.State>
        get() = combine(
            _navigationDestination,
            _screens
        ) { destination, screens ->
            HomeViewModel.State(screens = screens, destination = destination)
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, HomeViewModel.State())

    override fun onEvent(event: HomeViewModel.Event) {
        when (event) {
            is HomeViewModel.Event.NavigateTo -> _navigationDestination.value = event.destination
        }
    }


}

interface HomeViewModel: SingleFlowViewModel<HomeViewModel.Event, HomeViewModel.State> {

    sealed class Event {
        data class NavigateTo(val destination: Destinations): Event()
    }

    data class State(
        val screens: List<Screen> = emptyList(),
        val destination: Destinations? = null
    )

}

val LocalHomeViewModel = compositionLocalOf<HomeViewModel> {
    error("HomeViewModel not provided")
}

@Composable
fun provideHomeViewModelFactory(viewModelFactory: @Composable () -> HomeViewModel)
    = LocalHomeViewModel provides viewModelFactory.invoke()