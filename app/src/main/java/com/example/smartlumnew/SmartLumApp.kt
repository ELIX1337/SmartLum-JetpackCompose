package com.example.smartlumnew

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
    ProvideWindowInsets {
        SmartLumTheme {
            val navController = rememberAnimatedNavController()
            val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
            val scope = rememberCoroutineScope()
            var canPop by remember { mutableStateOf(false) }
            var currentRoute by remember { mutableStateOf("") }
            var showNavigationTopBar by remember { mutableStateOf(true) }

            DisposableEffect(navController) {
                val callback = NavController.OnDestinationChangedListener { controller, destination, _ ->
                    canPop = controller.currentDestination?.parent?.route != MainDestinations.HOME_GRAPH_ROUTE
                    currentRoute = controller.currentDestination?.route.toString()
                    showNavigationTopBar = when (destination.parent?.route) {
                        MainDestinations.HOME_GRAPH_ROUTE -> true
                        else -> false
                    }
                }
                navController.addOnDestinationChangedListener(callback)
                onDispose {
                    navController.removeOnDestinationChangedListener(callback)
                }
            }

            Scaffold(
                scaffoldState = scaffoldState,
                drawerContent = {
                    DrawerContent(
                        modifier = Modifier.statusBarsPadding(),
                        scaffoldState = scaffoldState,
                        navController = navController
                    ) },
                drawerGesturesEnabled = !canPop,
            ) {
                AppNavigation(
                    Modifier
                        .padding(0.dp, appBarHeight, 0.dp, 0.dp)
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    scannerViewModel,
                    navController,
                )
                AnimatedVisibility(
                    visible = showNavigationTopBar,
                    enter = slideInVertically(),
                    exit = slideOutVertically(),
                ) {
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

@Composable
fun NavigationTopBar(
    titleText: String? = null,
    progressIndicator: Boolean = false,
    canPopBack: Boolean,
    openDrawer: () -> Unit = { },
    navigateUp: () -> Unit = { },
) {

    Column {
        TransparentTopBar(
            title = { Text(titleText ?: stringResource(R.string.app_name)) },
            navigationIcon = {
                IconButton(
                    onClick = if (canPopBack) navigateUp else openDrawer) {
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
