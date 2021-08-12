package com.example.smartlumnew.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.smartlumnew.R

sealed class Screen(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int) {

    object Home : Screen("HomeScreen", R.string.app_name, R.drawable.ic_baseline_home_24)
    object Scanner : Screen("ScannerScreen", R.string.app_name, R.drawable.ic_baseline_search_24)
    object Settings : Screen("SettingsScreen", R.string.app_name, R.drawable.ic_baseline_settings_24)
    object Peripheral: Screen("PeripheralScreen", R.string.app_name, R.drawable.ic_baseline_search_24)
}