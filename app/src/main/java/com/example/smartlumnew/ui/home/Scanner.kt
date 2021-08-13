package com.example.smartlumnew.ui.home

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.R
import com.example.smartlumnew.models.bluetooth.DiscoveredBluetoothDevice
import com.example.smartlumnew.models.viewModels.ScannerViewModel
import com.example.smartlumnew.ui.components.BluetoothEnableRequestSheet
import com.example.smartlumnew.ui.components.DiscoveredPeripheralsList
import com.example.smartlumnew.ui.components.LocationEnableRequestSheet
import com.example.smartlumnew.ui.components.PermissionRequestSheet
import com.example.smartlumnew.utils.Utils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@ExperimentalMaterialApi
@ExperimentalPermissionsApi
@Composable
fun Scanner(
    scannerViewModel: ScannerViewModel,
    onPeripheralSelected: (DiscoveredBluetoothDevice) -> Unit
) {
    val context = LocalContext.current
    val isScanning by scannerViewModel.isScanning.observeAsState(false)
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
            scannerViewModel.stopScan()
        }
    }
    else {
        Log.e("TAG", "ScannerScreen: collapsing")
        if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
            LaunchedEffect(bottomSheetScaffoldState.bottomSheetState) {
                bottomSheetScaffoldState.bottomSheetState.collapse()
            }
        }
        if (!isScanning) {
            scannerViewModel.startScan()
        }
    }

}