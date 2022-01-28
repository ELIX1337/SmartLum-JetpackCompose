package com.example.smartlumnew.ui.peripheral

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.smartlumnew.R
import com.example.smartlumnew.models.bluetooth.DiscoveredPeripheral
import com.example.smartlumnew.models.data.PeripheralProfileEnum
import com.example.smartlumnew.models.viewModels.FLClassicViewModel
import com.example.smartlumnew.models.viewModels.PeripheralViewModel
import com.example.smartlumnew.models.viewModels.SLBaseViewModel
import com.example.smartlumnew.models.viewModels.SLProStandartViewModel

@Composable
fun PeripheralSettingsScreen(
    modifier: Modifier = Modifier,
    peripheral: DiscoveredPeripheral,
    viewModel: PeripheralViewModel,
    navigateUp: () -> Unit,
    onResetClicked: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val error           by viewModel.error.observeAsState()
    var showErrorDialog by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = modifier) {
            error?.let { PeripheralErrorCell { showErrorDialog = true } }
            PeripheralSettingsScreen(
                modifier = modifier,
                peripheral = peripheral,
                viewModel = viewModel,
                resetSettings = { showDialog = true }
            )
        }

        PeripheralResetDialog(
            isOpen = showDialog,
            dismiss = { showDialog = false },
            confirm = {
                viewModel.resetToFactorySettings()
                showDialog = false
                onResetClicked()
            }
        )
        PeripheralTopBar(
            title = stringResource(R.string.peripheral_settings_screen_title),
            navigateUp = navigateUp,
            openPeripheralSettings = { },
            showActions = false
        )
        error?.let {
            PeripheralErrorDialog(
                error = it,
                isOpen = showErrorDialog) {
                showErrorDialog = false
            }
        }
    }
}

@Composable
fun PeripheralResetDialog(
    isOpen: Boolean,
    dismiss: () -> Unit,
    confirm: () -> Unit,
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = dismiss,
            title = {
                Text(stringResource(R.string.alert_dialog_reset_title))
            },
            text = {
                Text(stringResource(R.string.alert_dialog_reset_text))
            },
            confirmButton = {
                Button(confirm) {
                    Text(stringResource(R.string.alert_dialog_reset_confirm))
                }
            },
            dismissButton = {
                Button(dismiss) {
                    Text(stringResource(R.string.alert_dialog_reset_dismiss))
                }
            }
        )
    }
}

@Composable
internal fun PeripheralSettingsScreen(
    modifier: Modifier = Modifier,
    peripheral: DiscoveredPeripheral,
    viewModel: PeripheralViewModel,
    resetSettings: () -> Unit,
) {
    when (peripheral.type) {
        PeripheralProfileEnum.FL_CLASSIC -> FLClassicSettingsScreen(viewModel as FLClassicViewModel)
        PeripheralProfileEnum.FL_MINI    -> FLClassicSettingsScreen(viewModel as FLClassicViewModel)
        PeripheralProfileEnum.SL_BASE    -> SLBaseSettingsScreen(modifier, viewModel as SLBaseViewModel, resetSettings)
        PeripheralProfileEnum.SL_PRO -> SLProSettingsScreen(modifier, viewModel as SLProStandartViewModel, resetSettings)
        else -> { Text("HZ") }
    }
}