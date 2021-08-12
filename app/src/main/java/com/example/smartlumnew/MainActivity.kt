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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartlumnew.bluetooth.DiscoveredBluetoothDevice
import com.example.smartlumnew.ui.MainScreen
import com.example.smartlumnew.ui.components.*
import com.example.smartlumnew.utils.Utils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

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
    val context = LocalContext.current
    val scannerViewModel: ScannerViewModel = viewModel()
    val isScanning by scannerViewModel.isScanning.observeAsState(false)
    val isBluetoothEnabled by scannerViewModel.isBluetoothEnabled.observeAsState(Utils.isBleEnabled(context))
    val isLocationEnabled by scannerViewModel.isLocationEnabled.observeAsState(Utils.isLocationEnabled(context))
    val isLocationPermissionGranted by scannerViewModel.isLocationGranted.observeAsState(Utils.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION))
    val requiredPermissions = rememberMultiplePermissionsState(listOf(Manifest.permission.ACCESS_FINE_LOCATION))
    val scanResult by scannerViewModel.scanResult.observeAsState()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed))
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
                        stringResource(R.string.location_permission_request),
                        stringResource(R.string.location_permission_denied),
                        isLocationPermissionGranted,
                        requiredPermissions,
                        onPermissionGranted = { scannerViewModel.setLocationPermissionStatus(it) }
                    )
                }
            }
        }
    ) {
        DiscoveredPeripheralsList(scanResult, onPeripheralSelected)
    }

    if (!isBluetoothEnabled || !isLocationEnabled || !isLocationPermissionGranted) {
        LaunchedEffect(bottomSheetScaffoldState.bottomSheetState) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
        if (isScanning) {
            //scannerViewModel.stopScan()
        }
    }
    else if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
        Log.e("TAG", "ScannerScreen: collapsing")
        LaunchedEffect(bottomSheetScaffoldState.bottomSheetState) {
            bottomSheetScaffoldState.bottomSheetState.collapse()
        }
        if (!isScanning) {
            //scannerViewModel.startScan()
        }
    }

}