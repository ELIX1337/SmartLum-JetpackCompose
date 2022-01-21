package com.example.smartlumnew

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartlumnew.models.viewModels.ScannerViewModel
import com.example.smartlumnew.navigation.AppNavigation
import com.example.smartlumnew.navigation.HomeGraphDestinations
import com.example.smartlumnew.navigation.MainDestinations
import com.example.smartlumnew.ui.components.AnimatedContentSwitch
import com.example.smartlumnew.ui.components.DrawerContent
import com.example.smartlumnew.ui.components.TransparentTopBar
import com.example.smartlumnew.ui.theme.SmartLumTheme
import com.example.smartlumnew.ui.theme.appBarHeight
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SmartLumApp(
    scannerViewModel: ScannerViewModel,
) {
    // Убираем отступы от системных элементов
    ProvideWindowInsets {

        // Оборачиваем все приложение в собственную тему
        SmartLumTheme {

            // Позволяет реализовать навигацию по приложению
            val navController = rememberAnimatedNavController()

            val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
            val scope = rememberCoroutineScope()

            // 1. Жест "назад" и "открыть левое меню" идентичны
            // 2. Открывать меню нужно только в главных экранах этого меню (сканнер, настройки и тд.), а не во вложенных
            // Поэтому с помощью этого boolean мы заблокируем возможность открытия меню, например, на экране устройства.
            var canPop by remember { mutableStateOf(false) }

            // Текущая точка назначения в навигации
            var currentRoute by remember { mutableStateOf("") }

            var showNavigationTopBar by remember { mutableStateOf(true) }

            // Отслеживаем где находится фокус
            // Используется чтобы сбросить фокус, например, когда пользователь вводил данные в TextView,
            // и нажал на экран чтобы пропал указатель с этого TextView
            var focusManager = LocalFocusManager.current

            DisposableEffect(navController) {

                // Отслеживаем навигацию по приложению
                val callback = NavController.OnDestinationChangedListener { controller, destination, _ ->

                    // Если мы не находимся в главном графе (перешли к эрану приложения и тп.)
                    canPop = controller.currentDestination?.parent?.route != MainDestinations.HOME_GRAPH_ROUTE
                    currentRoute = controller.currentDestination?.route.toString()

                    // Меню сверху у навигации отдельное
                    // Ссделано это для того:
                    // 1. Чтобы его можно было скрыть на других экранах
                    // 2. Другие экраны могли реализовывать собственное меню сверху
                    showNavigationTopBar = when (destination.parent?.route) {
                        MainDestinations.HOME_GRAPH_ROUTE -> true
                        else -> false
                    }
                }

                // Вешаем слушатель
                navController.addOnDestinationChangedListener(callback)

                // Удаляем слушатель при уничтожении экрана
                onDispose {
                    navController.removeOnDestinationChangedListener(callback)
                }
            }

            // Самый главный контейнер в приложении
            Scaffold(
                modifier = Modifier
                    // Вешаем фокус менеджер
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    },
                scaffoldState = scaffoldState,
                drawerContent = {
                    DrawerContent(
                        modifier = Modifier.statusBarsPadding(),
                        scaffoldState = scaffoldState,
                        navController = navController
                    ) },
                drawerGesturesEnabled = !canPop,
            ) {

                // Все остальное приложение тут
                /**
                 * ВНИМАНИЕ: Так как о существовании верхнего меню (TopBar'а) приложение не знает (читай ниже),
                 * то и отступы для него нужно делать вручную.
                 * Они передаются через этот модификатор.
                 * Используй его дальше чтобы не городить новый modifier на каждый элемент.
                 */
                AppNavigation(
                    Modifier
                        .padding(0.dp, appBarHeight, 0.dp, 0.dp)
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    scannerViewModel,
                    navController,
                )

                // Верхнее меню
                AnimatedVisibility(
                    visible = showNavigationTopBar,
                    enter = slideInVertically(),
                    exit = slideOutVertically(),
                ) {

                    /**
                     * Кастомное меню
                     * Может возникнуть вопрос - Почему просто не засунуть его в Scaffold? (там есть готовый аргумент topBar)
                     * Тогда ведь все отступы и верстка будут просчитываться автоматически!
                     * Ответ: Можно
                     * Но тогда нельзя будет реализовать фичу когда контент может "заезжать" под меню
                     * (для этого мы и убирали отступы от системных баров)
                     * В данной реализации, если скроллить контент, то он будет скроллиться до самого края экрана смартфона,
                     * в случае, если реализовать меню через Scaffold, то контент будет обрезаться под меню.
                     * Получается мы может сделать меню полупрозрачным и будет визуально больше места и в целом красиво.
                     * Можно проверить это на экране сканнера
                     * (нужно отключить скан-фильтр чтобы экран забился всеми устройствами поблизости и включился скролл)
                     */
                    NavigationTopBar(
                        titleText = HomeGraphDestinations.valueOf(route = currentRoute)?.getTitle(),
                        progressIndicator = scannerViewModel.isScanning.observeAsState().value!!,
                        canPopBack = canPop,
                        openDrawer = { scope.launch { scaffoldState.drawerState.open() } },
                        navigateUp = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}

/**
 * Описал его тут, потому-что используется только тут
 */
@Composable
fun NavigationTopBar(
    titleText: String? = null,
    progressIndicator: Boolean = false,
    canPopBack: Boolean,
    openDrawer: () -> Unit = { },
    navigateUp: () -> Unit = { },
) {

    Column {

        // Кастомный бар
        TransparentTopBar(
            title = { Text(titleText ?: stringResource(R.string.app_name)) },
            navigationIcon = {
                IconButton(
                    onClick = if (canPopBack) navigateUp else openDrawer) {

                    // Сделал свой анимирующий Composable чтобы было удобно анимировать смену 2 элементов
                    AnimatedContentSwitch(
                        toggleContent = canPopBack,
                        contentFrom = { Icon(Icons.Rounded.Menu, "Menu") },
                        contentTo = { Icon(Icons.Rounded.ArrowBack, "Back") }
                    )
                }
            }
        )
        if (progressIndicator) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
