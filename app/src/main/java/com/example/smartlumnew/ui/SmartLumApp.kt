package com.example.smartlumnew.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartlumnew.SmartLumNavGraph
import com.example.smartlumnew.models.viewModels.ScannerViewModel
import com.example.smartlumnew.ui.home.BottomNavigationBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@ExperimentalMaterialApi
@ExperimentalPermissionsApi
@Composable
fun SmartLumApp(scannerViewModel: ScannerViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            //if (currentRoute(navController) != Screen.Peripheral.route) {
                BottomNavigationBar(navController)
            //}
        }
    ) {
        SmartLumNavGraph(scannerViewModel,Modifier.padding(it),navController)
    }
}