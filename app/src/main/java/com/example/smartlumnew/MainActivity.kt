package com.example.smartlumnew

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.viewModels.ScannerViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartlumnew.bluetooth.DiscoveredBluetoothDevice
import com.example.smartlumnew.ui.components.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val scannerViewModel: ScannerViewModel by viewModels()

    @ExperimentalMaterialApi
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    override fun onStop() {
        super.onStop()
        scannerViewModel.stopScan()
    }

}

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

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Scanner.route) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Scanner.route) {
            ScannerScreen(
                onPeripheralSelected = {
                    Log.e("TAG", "NavigationHost: CONNECTION EVENT WITH ${it.name}")
                    navController.navigate(Screen.Peripheral.route)
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.Peripheral.route) {
            PeripheralScreen { navController.popBackStack() }
        }
    }
}

@Composable
fun HomeScreen() {
    Box(contentAlignment = Alignment.Center,
    modifier = Modifier.fillMaxSize()) {
    Text(
        text = "Home screen",
        fontSize = 30.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxSize())
}
}

@Composable
fun SettingsScreen() {
    Box(contentAlignment = Alignment.Center) {
    Text(
        text = "Settings screen",
        fontSize = 30.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun PeripheralScreen(onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        Button(onClick) {
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalPermissionsApi
@Composable
fun ScannerScreen(onPeripheralSelected: (DiscoveredBluetoothDevice) -> Unit) {
    val scannerViewModel: ScannerViewModel = viewModel()
    val isScanning by scannerViewModel.isScanning.observeAsState(false)
    val isBluetoothEnabled by scannerViewModel.isBluetoothEnabled.observeAsState(false)
    val isLocationEnabled by scannerViewModel.isLocationEnabled.observeAsState(false)
    val isLocationPermissionGranted by scannerViewModel.isLocationGranted.observeAsState(false)
    val requiredPermissions = rememberMultiplePermissionsState(listOf(Manifest.permission.ACCESS_FINE_LOCATION))
    val scanResult by scannerViewModel.scanResult.observeAsState()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed))
    val coroutineScope = rememberCoroutineScope()
    Scaffold {
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetPeekHeight = 0.dp,
            sheetShape = RoundedCornerShape(24.dp, 24.dp),
            sheetGesturesEnabled = false,
            sheetElevation = 24.dp,
            sheetContent = {
                Box(modifier = Modifier.padding(24.dp)) {
                    when {
                        !isBluetoothEnabled -> BluetoothEnableRequestSheet(isBluetoothEnabled)
                        !isLocationEnabled -> LocationEnableRequestSheet(isLocationEnabled)
                        !isLocationPermissionGranted -> PermissionRequestSheet(
                            "From Android 6.0 Marshmallow onwards applications requires Location permission" +
                                    "in order to scan for Bluetooth Low Energy devices",
                            "From Android 6.0 Marshmallow onwards applications requires Location permission" +
                                    "in order to scan for Bluetooth Low Energy devices." +
                                    "\n\nThis is because Bluetooth LE beacons may be used to determine the phone's and user's location",
                            isLocationPermissionGranted,
                            requiredPermissions,
                            onPermissionGranted = { scannerViewModel.setLocationPermissionStatus(it) })
                    }
                }
            } ) {
            when {
                (!isBluetoothEnabled || !isLocationEnabled || !isLocationPermissionGranted) -> {
                    Log.e("TAG", "ScannerScreen: expanding")
                    coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.expand() }
                    scannerViewModel.stopScan()
                }
                (!isScanning)-> {
                    Log.e("TAG", "ScannerScreen: collapsing")
                    coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.collapse() }
                    scannerViewModel.startScan()
                }
            }
            DiscoveredPeripheralsList(scanResult, onPeripheralSelected)
        }
    }
}