package com.example.smartlumnew.ui.home

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.R
import com.example.smartlumnew.models.bluetooth.DiscoveredPeripheral
import com.example.smartlumnew.models.viewModels.ScannerViewModel
import com.example.smartlumnew.ui.components.BluetoothEnableRequestSheet
import com.example.smartlumnew.ui.components.LocationEnableRequestSheet
import com.example.smartlumnew.ui.components.PeripheralsList
import com.example.smartlumnew.ui.components.PermissionRequestSheet
import com.example.smartlumnew.utils.Utils
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterialApi::class,
    ExperimentalPermissionsApi::class)
@Composable
fun Scanner(
    modifier: Modifier = Modifier,
    scannerViewModel: ScannerViewModel,
    onPeripheralSelected: (DiscoveredPeripheral) -> Unit,
) {
    val context = LocalContext.current
    val isScanning by scannerViewModel.isScanning.observeAsState(false)
    val isRefreshing by scannerViewModel.isRefreshing.observeAsState(false)
    val isBluetoothEnabled by scannerViewModel.isBluetoothEnabled.observeAsState(Utils.isBleEnabled(context))
    val isLocationEnabled by scannerViewModel.isLocationEnabled.observeAsState(Utils.isLocationEnabled(context))
    val isLocationPermissionGranted by scannerViewModel.isLocationGranted.observeAsState(Utils.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION))
    val requiredPermissions = rememberMultiplePermissionsState(listOf(Manifest.permission.ACCESS_FINE_LOCATION))
    val scanResult by scannerViewModel.scanResult.observeAsState()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = rememberBottomSheetState(
        BottomSheetValue.Collapsed)
    )
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(24.dp, 24.dp),
        sheetGesturesEnabled = false,
        sheetElevation = 24.dp,
        sheetContent = {
            Box(modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding()) {
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
        },
    ) { contentPadding ->
        DisposableEffect(Unit) {
            onDispose {
                scannerViewModel.stopScan()
            }
        }
        Box {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing),
                    indicatorPadding = PaddingValues(dimensionResource(R.dimen.AppBar_height) + dimensionResource(R.dimen.StatusBar_height)),
                    refreshTriggerDistance = 100.dp,
                    onRefresh = { scannerViewModel.refresh() }
                ) {
                    PeripheralsList(
                        Modifier.padding(8.dp, 0.dp),
                        scanResult,
                        onPeripheralSelected,
                        contentPadding)
                }
            }
        }
    }

    if (!isBluetoothEnabled || !isLocationEnabled || !isLocationPermissionGranted) {
        LaunchedEffect(bottomSheetScaffoldState.bottomSheetState) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
        if (isScanning) {
            scannerViewModel.stopScan()
        }
    }
    else if (!bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
        Log.e("TAG", "ScannerScreen: collapsing")
        LaunchedEffect(bottomSheetScaffoldState.bottomSheetState) {
            bottomSheetScaffoldState.bottomSheetState.collapse()
        }
    } else {
        scannerViewModel.startScan()
    }

}