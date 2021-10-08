package com.example.smartlumnew.ui.peripheral

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartlumnew.models.bluetooth.ConnectionState
import com.example.smartlumnew.models.bluetooth.DiscoveredPeripheral
import com.example.smartlumnew.models.bluetooth.PeripheralProfileEnum
import com.example.smartlumnew.models.viewModels.FLClassicViewModel
import com.example.smartlumnew.models.viewModels.PeripheralViewModel
import com.example.smartlumnew.models.viewModels.PeripheralViewModelFactory
import com.example.smartlumnew.models.viewModels.SLBaseViewModel
import com.example.smartlumnew.ui.components.ConnectingScreen
import com.example.smartlumnew.ui.components.TransparentTopBar

@Composable
fun PeripheralScreen(
    modifier: Modifier = Modifier,
    peripheral: DiscoveredPeripheral,
    viewModel: PeripheralViewModel = viewModel(
        factory = PeripheralViewModelFactory(
            LocalContext.current.applicationContext as Application,
            peripheral.type
        )
    ),
    navigateUp: () -> Unit,
    openPeripheralSettings: (DiscoveredPeripheral, PeripheralViewModel) -> Unit,
) {
    viewModel.connect(peripheral)
    val connectionState by viewModel.connectionState.observeAsState(ConnectionState.CONNECTING)

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
//        Spacer(
//            Modifier
//                .padding(0.dp, dimensionResource(id = R.dimen.AppBar_height), 0.dp, 0.dp)
//                .statusBarsPadding())
        PeripheralScreen(
            modifier = modifier,
            viewModel = viewModel,
            peripheral = peripheral,
            connectionState = connectionState,
        )
        //Spacer(Modifier.navigationBarsPadding())
    }
    PeripheralTopBar(
        title = stringResource(peripheral.type.peripheralName),
        navigateUp = { navigateUp() },
        openPeripheralSettings = { openPeripheralSettings(peripheral, viewModel) },
        showActions = viewModel.isInitialized.observeAsState(false).value
    )
}

@Composable
private fun PeripheralScreen(
    modifier: Modifier = Modifier,
    viewModel: PeripheralViewModel,
    peripheral: DiscoveredPeripheral,
    connectionState: ConnectionState,
) {
    if (connectionState == ConnectionState.READY) {
        PeripheralScreen(
            modifier = modifier,
            viewModel = viewModel,
            peripheralType = peripheral.type,
        )
    } else {
        ConnectingScreen(connectionState)
    }
}

@Composable
private fun PeripheralScreen(
    modifier: Modifier = Modifier,
    viewModel: PeripheralViewModel,
    peripheralType: PeripheralProfileEnum,
) {
    val isInitialized by viewModel.isInitialized.observeAsState()

    if (isInitialized == true) {
        PeripheralReadyScreen(modifier, peripheralType, viewModel)
    } else if (isInitialized == false) {
        PeripheralSetupScreen(modifier, peripheralType, viewModel)
    }
}

@Composable
fun PeripheralReadyScreen(
    modifier: Modifier = Modifier,
    peripheralType: PeripheralProfileEnum,
    viewModel: PeripheralViewModel
) {
    when (peripheralType) {
        PeripheralProfileEnum.FL_CLASSIC -> FLClassic(modifier, viewModel as FLClassicViewModel)
        PeripheralProfileEnum.FL_MINI -> FLClassic(modifier, viewModel as FLClassicViewModel)
        PeripheralProfileEnum.SL_BASE -> SLBaseMainScreen(modifier, viewModel as SLBaseViewModel)
        PeripheralProfileEnum.UNKNOWN -> Text("Unknown device")
    }
}

@Composable
fun PeripheralSetupScreen(
    modifier: Modifier = Modifier,
    peripheralType: PeripheralProfileEnum,
    viewModel: PeripheralViewModel,
) {
    Column {
        PeripheralSetupScreen(
            peripheralType = peripheralType,
            viewModel = viewModel,
        )
    }
}

@Composable
private fun PeripheralSetupScreen(
    peripheralType: PeripheralProfileEnum,
    viewModel: PeripheralViewModel,
) {
    when (peripheralType) {
        PeripheralProfileEnum.FL_CLASSIC -> FLClassicSettingsScreen(viewModel as FLClassicViewModel)
        PeripheralProfileEnum.FL_MINI    -> FLClassicSettingsScreen(viewModel as FLClassicViewModel)
        PeripheralProfileEnum.SL_BASE    -> SLBaseSetupScreen(viewModel as SLBaseViewModel)
        PeripheralProfileEnum.UNKNOWN    -> Text("Unknown device")
    }
}

@Composable
fun PeripheralTopBar(
    title: String,
    navigateUp: () -> Unit,
    openPeripheralSettings: () -> Unit,
    showActions: Boolean,
) {
    TransparentTopBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(
                onClick = navigateUp,
            ) {
                Icon(Icons.Rounded.ArrowBack, "Back")
            }
        },
        actions = { if (showActions)
            IconButton(
                onClick = openPeripheralSettings,
            ) {
                Icon(Icons.Rounded.Settings, "Device settings")
            }
        }
    )
}