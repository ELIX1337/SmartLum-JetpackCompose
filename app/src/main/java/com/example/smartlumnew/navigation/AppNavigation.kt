package com.example.smartlumnew.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smartlumnew.models.bluetooth.DiscoveredPeripheral
import com.example.smartlumnew.models.viewModels.ScannerViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

/**
 * Навигация приложения разбивается на 2 ветки (графа):
 * 1.HOME - Сканнер, Настройки (дальше можно еще добавлять)
 * 2.PERIPHERAL - Основной экран устройства, экран расширенных настроек, экран первичной настройки (инициализации)
 * Подробнее как это работает можно помотреть в документации для Navigation
 */
object MainDestinations {
    const val HOME_GRAPH_ROUTE = "home"
    const val PERIPHERAL_GRAPH_ROUTE = "peripheral"
}

/**
 *
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    scannerViewModel: ScannerViewModel,
    navController: NavHostController,
    startDestination: String = MainDestinations.HOME_GRAPH_ROUTE,
) {
    // Так как экран сканнера и экран работы с устройством находятся в разных графах,
    // То нужно это самое устройство "пронести" через навигацию
    // Я пытался это сделать через аргументы (см. NavArguments в документации),
    // Но нормально закодировать класс Peripheral для этого не вышло
    var destinationPeripheral by remember { mutableStateOf<DiscoveredPeripheral?>(null) }

    // Анимированный NavHost (экспериментальный API)
    // В дефолтной библиотеке, на момент написания, навигация шла без анимаций (прикинь, да)
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // Первая ветка навигации
        navigation(
            route = MainDestinations.HOME_GRAPH_ROUTE,
            startDestination = HomeGraphDestinations.SCANNER.route
        ) {
            addHomeGraph(
                modifier,
                navController,
                scannerViewModel,
            ) { peripheral ->
                // Получаем устройство, по которому кликнули в сканнере и сохраняем его в переменную
                destinationPeripheral = peripheral

                // Переходим на экран устройства
                // Для этого заходим в соответствующий граф
                // Проверка if нужна для того, чтобы обработать множественные клики по устройству:
                // если быстро и много кликать по найденному устройству (задержка навигации ведь есть),
                // тем самым постоянно вызывая navigate(),
                // то будет много экранов
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

        // Вторая ветка навигации
        navigation(
            route = MainDestinations.PERIPHERAL_GRAPH_ROUTE,
            startDestination = PeripheralGraphDestinations.MAIN.route
        ) {
            addPeripheralGraph(
                modifier = modifier,
                navController = navController,
                peripheral = { destinationPeripheral!! },
                // Были утечки памяти во ViewModel, поэтому я тут тестировал ее контекст.
                // Вроде пофиксил, но оставлю тут как напоминание
                // лямбда - потому-что как обычный аргумент не сработает (долго обьяснять)
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

/**
 * Кастомный Destination для навигации.
 * Кастомные тут только анимации.
 * Просто сделал отдельным Composable чтобы каждый раз не писать анимации в аргументы
 */
@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.smartlumComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) },
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) },
    popEnterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) },
    popExitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) },
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

// Это не нашло применения
fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

@Composable
fun NavHostController.currentRoute(): String? {
    val navBackStackEntry by this.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}