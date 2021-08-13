package com.example.smartlumnew

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.navigation
import com.example.smartlumnew.MainDestinations.PERIPHERAL_TYPE_KEY
import com.example.smartlumnew.models.viewModels.ScannerViewModel
import com.example.smartlumnew.ui.home.HomeDestinations
import com.example.smartlumnew.ui.home.addHomeGraph
import com.example.smartlumnew.ui.peripheral.PeripheralScreen
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
                { peripheral ->
                    navController.navigate("${MainDestinations.PERIPHERAL_SCREEN_ROUTE}/${peripheral.address}")
                },
                scannerViewModel = scannerViewModel
            )
        }
        composable(
            "${MainDestinations.PERIPHERAL_SCREEN_ROUTE}/{$PERIPHERAL_TYPE_KEY}",
            arguments = listOf(navArgument(PERIPHERAL_TYPE_KEY) { type = NavType.LongType })
        ){
            PeripheralScreen {

            }
        }
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED