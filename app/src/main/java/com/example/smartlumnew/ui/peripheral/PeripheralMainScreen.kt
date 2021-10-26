package com.example.smartlumnew.ui.peripheral

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartlumnew.R
import com.example.smartlumnew.models.bluetooth.ConnectionState
import com.example.smartlumnew.models.bluetooth.DiscoveredPeripheral
import com.example.smartlumnew.models.bluetooth.PeripheralProfileEnum
import com.example.smartlumnew.models.data.PeripheralError
import com.example.smartlumnew.models.viewModels.FLClassicViewModel
import com.example.smartlumnew.models.viewModels.PeripheralViewModel
import com.example.smartlumnew.models.viewModels.PeripheralViewModelFactory
import com.example.smartlumnew.models.viewModels.SLBaseViewModel
import com.example.smartlumnew.ui.components.Cell
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

    PeripheralScreen(
        modifier = modifier.fillMaxSize(),
        viewModel = viewModel,
        peripheral = peripheral
    )
    PeripheralTopBar(
        title = stringResource(peripheral.type.peripheralName),
        navigateUp = navigateUp,
        openPeripheralSettings = { openPeripheralSettings(peripheral, viewModel) },
        showActions = viewModel.isInitialized.observeAsState(false).value && viewModel.hasOptions.observeAsState(
            initial = false
        ).value
    )
}

@Composable
private fun PeripheralScreen(
    modifier: Modifier = Modifier,
    viewModel: PeripheralViewModel,
    peripheral: DiscoveredPeripheral
) {
    val connectionState by viewModel.connectionState.observeAsState(ConnectionState.CONNECTING)
    PeripheralScreen(
        modifier = modifier,
        viewModel = viewModel,
        peripheral = peripheral,
        connectionState = connectionState
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
    val isInitialized   by viewModel.isInitialized.observeAsState()
    val error           by viewModel.error.observeAsState()
    var showErrorDialog by remember { mutableStateOf(false) }

    if (isInitialized == true) {
        Column(modifier //modifier.verticalScroll(rememberScrollState())
        ) {
            error?.let { PeripheralErrorCell { showErrorDialog = true } }
            PeripheralReadyScreen(peripheralType, viewModel)
        }
    } else if (isInitialized == false) {
        PeripheralSetupScreen(modifier, peripheralType, viewModel)
    }

    error?.let {
        PeripheralErrorDialog(
            error = it,
            isOpen = showErrorDialog) {
            showErrorDialog = false
        }
    }
}

@Composable
fun PeripheralReadyScreen(
    peripheralType: PeripheralProfileEnum,
    viewModel: PeripheralViewModel
) {
    when (peripheralType) {
        PeripheralProfileEnum.FL_CLASSIC -> FLClassic(viewModel as FLClassicViewModel)
        PeripheralProfileEnum.FL_MINI    -> FLClassic(viewModel as FLClassicViewModel)
        PeripheralProfileEnum.SL_BASE    -> SLBaseMainScreen(viewModel as SLBaseViewModel)
        PeripheralProfileEnum.UNKNOWN    -> Text("Unknown device")
    }
}

@Composable
fun PeripheralSetupScreen(
    modifier: Modifier = Modifier,
    peripheralType: PeripheralProfileEnum,
    viewModel: PeripheralViewModel,
) {
    Column(modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.periheral_setup_screen_header),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center
        )
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

@Composable
fun PeripheralErrorCell(
    openInfo: () -> Unit
) {
    Cell(
        backgroundColor = MaterialTheme.colors.error,
        shape = MaterialTheme.shapes.large,
        mainContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.peripheral_error_cell_title),
                    color = MaterialTheme.colors.onError,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedButton(onClick = openInfo) {
                    Text(
                        text = stringResource(R.string.peripheral_error_cell_detail_button),
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    )
}

@Composable
fun PeripheralErrorDialog(
    error: PeripheralError,
    isOpen: Boolean,
    dismiss: () -> Unit,
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = dismiss,
            title = {
                Text(
                    text = stringResource(R.string.alert_dialog_peripheral_error_title),
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column {
                    Text(stringResource(R.string.alert_dialog_peripheral_error_code) + error.code)
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(error.description))
                }
            },
            confirmButton = {
                Button(dismiss) {
                    Text(stringResource(R.string.alert_dialog_peripheral_error_dismiss_button))
                }
            }
        )
    }
}