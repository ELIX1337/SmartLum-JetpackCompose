package com.example.smartlumnew.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smartlumnew.models.bluetooth.DiscoveredPeripheral
import com.example.smartlumnew.models.viewModels.ScannerViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

object MainDestinations {
    const val HOME_GRAPH_ROUTE = "home"
    const val PERIPHERAL_GRAPH_ROUTE = "peripheral"
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    scannerViewModel: ScannerViewModel,
    navController: NavHostController,
    startDestination: String = MainDestinations.HOME_GRAPH_ROUTE,
) {
    var destinationPeripheral by rememberSaveable { mutableStateOf<DiscoveredPeripheral?>(null) }
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        navigation(
            route = MainDestinations.HOME_GRAPH_ROUTE,
            startDestination = HomeGraphDestinations.SCANNER.route
        ) {
            addHomeGraph(
                modifier,
                navController,
                scannerViewModel,
            ) { peripheral ->
                destinationPeripheral = peripheral
                if (navController.currentDestination?.route != PeripheralGraphDestinations.MAIN.route) {
                    navController.navigate(
                        route = MainDestinations.PERIPHERAL_GRAPH_ROUTE,
                    ) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
        navigation(
            route = MainDestinations.PERIPHERAL_GRAPH_ROUTE,
            startDestination = PeripheralGraphDestinations.MAIN.route
        ) {
            addPeripheralGraph(
                modifier = modifier,
                navController = navController,
                peripheral = { destinationPeripheral!! },
                //viewModel = { viewModel },
                navigateUp = {
                    if (navController.currentDestination?.parent?.route != MainDestinations.HOME_GRAPH_ROUTE) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}

fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

@Composable
fun NavHostController.currentRoute(): String? {
    val navBackStackEntry by this.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.smartlumComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (
    AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition?
    )? = { _, _ ->
        slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(200))
    },
    exitTransition: (
    AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?
    )? = { _, _ ->
        slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(200))
    },
    popEnterTransition: (
    AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition?
    )? = { _, _ ->
        slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(200))
    },
    popExitTransition: (
    AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?
    )? = { _, _ ->
        slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(200))
    },
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        content = content
    )
}