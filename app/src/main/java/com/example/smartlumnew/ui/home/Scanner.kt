package com.example.smartlumnew.ui.home

import android.Manifest
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.R
import com.example.smartlumnew.models.bluetooth.DiscoveredPeripheral
import com.example.smartlumnew.models.viewModels.ScannerViewModel
import com.example.smartlumnew.ui.components.BluetoothEnableRequestSheet
import com.example.smartlumnew.ui.components.LocationEnableRequestSheet
import com.example.smartlumnew.ui.components.PeripheralsList
import com.example.smartlumnew.ui.components.PermissionRequestSheet
import com.example.smartlumnew.ui.theme.appBarHeight
import com.example.smartlumnew.ui.theme.statusBarHeight
import com.example.smartlumnew.utils.Utils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * Экран сканнера
 * Управляется ViewModel
 * Передает выбранное устройство вверх
 */
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
    // Потянув список вниз, сделается рефреш, но иногда, по непонятной мне причине, зависает и вообще хз
    val isRefreshing by scannerViewModel.isRefreshing.observeAsState(false)
    val isBluetoothEnabled by scannerViewModel.isBluetoothEnabled.observeAsState(Utils.isBleEnabled(context))
    // Чтобы Bluetooth работал, нужно включить геолокацию
    val isLocationEnabled by scannerViewModel.isLocationEnabled.observeAsState(Utils.isLocationEnabled(context))
    val isLocationPermissionGranted by scannerViewModel.isLocationGranted.observeAsState(Utils.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION))
    //  Экспериментальный API для получени разрешений (намного удобнее стандартных)
    val requiredPermissions = rememberMultiplePermissionsState(listOf(Manifest.permission.ACCESS_FINE_LOCATION))
    val scanResult by scannerViewModel.scanResult.observeAsState()

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = rememberBottomSheetState(
        BottomSheetValue.Collapsed)
    )

    // Не обычный Scaffold, а тот, у которого есть всплывающее снизу меню
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(24.dp, 24.dp),
        sheetGesturesEnabled = false,
        sheetElevation = 24.dp,
        // Контент всплывающего снизу меню (запрос разрешений и включения Bluetooth)
        sheetContent = {
            Box(modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding()
            ) {
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
                //verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Добавляет фичу "потянуть чтобы обновить"
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing),
                    indicatorPadding = PaddingValues(appBarHeight + statusBarHeight),
                    refreshTriggerDistance = 100.dp,
                    onRefresh = { scannerViewModel.refresh() }
                ) {
                    // Список найденных устройств
                    PeripheralsList(
                        Modifier.padding(8.dp, 0.dp),
                        scanResult,
                        onPeripheralSelected,
                        contentPadding)
                }
            }
        }
    }

    // Просто проверяем разрешения, состояние Bluetooth и геолокацию
    // Если что-то не работает - всплывает меню снизу
    // Если все работает (включилось) - закрываем меню
    if (!isBluetoothEnabled || !isLocationEnabled || !isLocationPermissionGranted) {
        LaunchedEffect(bottomSheetScaffoldState.bottomSheetState) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
        if (isScanning) {
            scannerViewModel.stopScan()
        }
    }
    else if (!bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
        LaunchedEffect(bottomSheetScaffoldState.bottomSheetState) {
            bottomSheetScaffoldState.bottomSheetState.collapse()
        }
    } else {
        scannerViewModel.startScan()
    }

}