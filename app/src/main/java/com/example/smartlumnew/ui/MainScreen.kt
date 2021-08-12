package com.example.smartlumnew.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartlumnew.NavigationHost
import com.example.smartlumnew.ui.components.BottomNavigationBar
import com.example.smartlumnew.ui.components.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@ExperimentalMaterialApi
@ExperimentalPermissionsApi
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            if (currentRoute(navController) != Screen.Peripheral.route) {
                BottomNavigationBar(navController)
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            NavigationHost(navController)
        }
    }
}