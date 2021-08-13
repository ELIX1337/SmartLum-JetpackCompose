package com.example.smartlumnew.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smartlumnew.R
import com.example.smartlumnew.models.bluetooth.DiscoveredPeripheral
import com.example.smartlumnew.models.viewModels.ScannerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi

enum class HomeDestinations(
    val route: String,
    val title: Int,
    @DrawableRes val icon: Int
) {
    FEED("home/feed", R.string.home_screen, R.drawable.ic_baseline_home_24),
    SCANNER("home/scanner", R.string.scanner_screen, R.drawable.ic_baseline_search_24),
    SETTINGS("home/settings", R.string.settings_screen, R.drawable.ic_baseline_settings_24),
}

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
fun NavGraphBuilder.addHomeGraph(
    onPeripheralSelected: (DiscoveredPeripheral) -> Unit,
    scannerViewModel: ScannerViewModel
) {
    composable(HomeDestinations.FEED.route) {
        Feed()
    }
    composable(HomeDestinations.SCANNER.route) {
        Scanner(
            onPeripheralSelected = onPeripheralSelected,
            scannerViewModel = scannerViewModel
        )
    }
    composable(HomeDestinations.SETTINGS.route) {
        Settings()
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = HomeDestinations.values()
    BottomNavigation {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = stringResource(item.title)) },
                label = { Text(stringResource(item.title)) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        // If true app is crashing when quick change to previous screen
                        //restoreState = true
                    }
                }
            )
        }
    }
}