package com.example.smartlumnew

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.smartlumnew.models.viewModels.ScannerViewModel
import com.example.smartlumnew.ui.home.HomeDestinations
import com.example.smartlumnew.ui.home.addHomeGraph
import com.google.accompanist.permissions.ExperimentalPermissionsApi

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val PERIPHERAL_SCREEN_ROUTE = "peripheral"
    const val PERIPHERAL_TYPE_KEY = "peripheralType"
}

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun SmartLumNavGraph(
    scannerViewModel: ScannerViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = MainDestinations.HOME_ROUTE
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        navigation(
            route = MainDestinations.HOME_ROUTE,
            startDestination = HomeDestinations.SCANNER.route
        ) {
            addHomeGraph(
                {
                    navController.navigate(MainDestinations.PERIPHERAL_SCREEN_ROUTE)
                },
                scannerViewModel = scannerViewModel
            )
        }
        composable(
            MainDestinations.PERIPHERAL_SCREEN_ROUTE
        ) {
            Button(onClick = {
                navController.navigate(HomeDestinations.SETTINGS.route) {
                    navController.graph.startDestinationRoute?.let {
                        popUpTo(it)
                    }
                    launchSingleTop = true
                }
            }
            ) {
                Text(
                    text = "Peripheral screen",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED