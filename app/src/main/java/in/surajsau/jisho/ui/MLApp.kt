package `in`.surajsau.jisho.ui

import `in`.surajsau.jisho.ui.digitalink.DigitalInkScreen
import `in`.surajsau.jisho.ui.styletransfer.StyleTransferScreen
import `in`.surajsau.jisho.ui.theme.JishoTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MLApp() {
    val navigationController = rememberNavController()
    
    JishoTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            NavHost(navController = navigationController, startDestination = Destinations.Home.value) {
                composable(route = Destinations.DigitalInk.value) { DigitalInkScreen(viewModel = hiltViewModel()) }
                composable(route = Destinations.StyleTransfer.value) { StyleTransferScreen() }
            }
        }
    }
}