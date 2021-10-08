package com.example.smartlumnew.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.example.smartlumnew.R
import com.example.smartlumnew.models.bluetooth.DiscoveredPeripheral
import com.example.smartlumnew.models.viewModels.PeripheralViewModel
import com.example.smartlumnew.ui.peripheral.PeripheralScreen
import com.example.smartlumnew.ui.peripheral.PeripheralSettingsScreen

enum class PeripheralGraphDestinations(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int
) {
    MAIN("peripheral/main", R.string.home_screen_title, R.drawable.ic_baseline_home_24),
    SETTINGS("peripheral/settings", R.string.scanner_screen_title, R.drawable.ic_baseline_search_24),
    SETUP("peripheral/initialize", R.string.settings_screen_title, R.drawable.ic_baseline_settings_24);

    @Composable
    fun getTitle(): String = stringResource(id = title)

    companion object {
        fun valueOf(route: String): PeripheralGraphDestinations? = values().find { it.route == route }
    }
}

fun NavGraphBuilder.addPeripheralGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    peripheral: () -> DiscoveredPeripheral,
    //viewModel: () -> PeripheralViewModel,
    navigateUp: () -> Unit,
) {
    lateinit var vm: PeripheralViewModel

    smartlumComposable(
        route = PeripheralGraphDestinations.MAIN.route,
    ) {
        PeripheralScreen(
            modifier = modifier,
            peripheral = peripheral(),
            //viewModel = viewModel(),
            navigateUp = navigateUp,
            openPeripheralSettings = { peripheral, vviewModel ->
                vm = vviewModel
                if (navController.currentDestination?.route != PeripheralGraphDestinations.SETTINGS.route) {
                    navController.navigate(PeripheralGraphDestinations.SETTINGS.route) {
                        launchSingleTop = true
                    }
                }
            }
        )
    }

    smartlumComposable(PeripheralGraphDestinations.SETTINGS.route) {
        PeripheralSettingsScreen(
            modifier = modifier,
            peripheral = peripheral(),
            viewModel = vm,
            navigateUp = { navController.popBackStack() },
            onResetClicked = {
                navController.popBackStack(HomeGraphDestinations.SCANNER.route, inclusive = false, false)
            }
        )
    }
}