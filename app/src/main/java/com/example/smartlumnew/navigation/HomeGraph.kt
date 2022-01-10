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
import com.example.smartlumnew.models.viewModels.ScannerViewModel
import com.example.smartlumnew.ui.home.Scanner
import com.example.smartlumnew.ui.home.Settings

enum class HomeGraphDestinations(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int
) {
    //FEED("home/feed", R.string.home_screen_title, R.drawable.ic_baseline_home_24),
    SCANNER("home/scanner", R.string.scanner_screen_title, R.drawable.ic_baseline_search_24),
    SETTINGS("home/settings", R.string.settings_screen_title, R.drawable.ic_baseline_settings_24);

    @Composable
    fun getTitle(): String = stringResource(id = title)

    companion object {
        fun valueOf(route: String): HomeGraphDestinations? =
            values().find { it.route == route }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.addHomeGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    scannerViewModel: ScannerViewModel,
    onPeripheralSelected: (DiscoveredPeripheral) -> Unit,
) {
//    smartlumComposable(HomeGraphDestinations.FEED.route) {
//        Feed()
//    }
    smartlumComposable(HomeGraphDestinations.SCANNER.route) {
        Scanner(
            modifier = modifier,
            scannerViewModel = scannerViewModel,
            onPeripheralSelected = onPeripheralSelected,
        )
    }
    smartlumComposable(HomeGraphDestinations.SETTINGS.route) {
        Settings(modifier, navController = navController)
    }
}