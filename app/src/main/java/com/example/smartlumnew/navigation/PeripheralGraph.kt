package com.example.smartlumnew.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
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

/**
 * Схема графа для устройств.
 */
enum class PeripheralGraphDestinations(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int
) {
    MAIN("peripheral/main", R.string.home_screen_title, R.drawable.ic_baseline_home_24),
    SETTINGS("peripheral/settings", R.string.scanner_screen_title, R.drawable.ic_baseline_search_24),

    // Этот destination не используется из-за того, что были какие-то бага с ViewModel во время навигации.
    // SetupScreen реализован как отдельный Composable внутри Main экрана.
    SETUP("peripheral/initialize", R.string.settings_screen_title, R.drawable.ic_baseline_settings_24);

    @Composable
    fun getTitle(): String = stringResource(id = title)

    companion object {
        fun valueOf(route: String): PeripheralGraphDestinations? = values().find { it.route == route }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.addPeripheralGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    peripheral: () -> DiscoveredPeripheral,
    navigateUp: () -> Unit,
) {
    // Сохраняем ViewModel здесь чтобы ее потом передавать между destination
    lateinit var vm: PeripheralViewModel

    smartlumComposable(
        route = PeripheralGraphDestinations.MAIN.route,
    ) {
        PeripheralScreen(
            modifier = modifier,
            peripheral = peripheral(),
            //viewModel = viewModel(),
            navigateUp = navigateUp,
            // Эта лямбда срабатывает при нажании на иконку расширенных настроек
            openPeripheralSettings = { _, viewModel ->
                // Сохраняем ViewModel в переменную
                vm = viewModel
                // Делаем навигацию на экран расширенных настроек
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
            // Хватаем сохраненную ранее (в лямбде выше) переменную
            viewModel = vm,
            navigateUp = { navController.popBackStack() },
            onResetClicked = {
                // Кидаем пользователя на главный экран при нажатии "Сброс до заводских"
                navController.popBackStack(HomeGraphDestinations.SCANNER.route, inclusive = false, false)
            }
        )
    }
}