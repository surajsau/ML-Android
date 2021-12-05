package `in`.surajsau.jisho.ui

import `in`.surajsau.jisho.ui.digitalink.DigitalInkScreen
import `in`.surajsau.jisho.ui.digitalink.DigitalInkViewModelImpl
import `in`.surajsau.jisho.ui.digitalink.provideDigitalInkViewModel
import `in`.surajsau.jisho.ui.home.HomeScreen
import `in`.surajsau.jisho.ui.home.HomeViewModelImpl
import `in`.surajsau.jisho.ui.home.provideHomeViewModelFactory
import `in`.surajsau.jisho.ui.styletransfer.StyleTransferScreen
import `in`.surajsau.jisho.ui.styletransfer.StyleTransferViewModelImpl
import `in`.surajsau.jisho.ui.styletransfer.provideStyleTransferViewModel
import `in`.surajsau.jisho.ui.theme.JishoTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MLApp(
    navigateToSettings: () -> Unit
) {
    val navigationController = rememberNavController()
    
    JishoTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            NavHost(navController = navigationController, startDestination = Destinations.Home.value) {
                composable(route = Destinations.Home.value) {
                    CompositionLocalProvider(
                        provideHomeViewModelFactory { hiltViewModel<HomeViewModelImpl>() }
                    ) {
                        HomeScreen(
                            navigateToDestination = { navigationController.navigate(route = it.value) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                composable(route = Destinations.DigitalInk.value) {
                    CompositionLocalProvider(
                        provideDigitalInkViewModel { hiltViewModel<DigitalInkViewModelImpl>() }
                    ) {
                        DigitalInkScreen(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                composable(route = Destinations.StyleTransfer.value) {
                    CompositionLocalProvider(
                        provideStyleTransferViewModel { hiltViewModel<StyleTransferViewModelImpl>() }
                    ) {
                        StyleTransferScreen(
                            modifier = Modifier.fillMaxSize(),
                            navigateBack = { navigationController.popBackStack() },
                            navigateToSettings = navigateToSettings
                        )
                    }
                }
            }
        }
    }
}