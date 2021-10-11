package com.example.smartlumnew.ui.peripheral

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.smartlumnew.R
import com.example.smartlumnew.models.bluetooth.DiscoveredPeripheral
import com.example.smartlumnew.models.bluetooth.PeripheralProfileEnum
import com.example.smartlumnew.models.viewModels.FLClassicViewModel
import com.example.smartlumnew.models.viewModels.PeripheralViewModel
import com.example.smartlumnew.models.viewModels.SLBaseViewModel

@Composable
fun PeripheralSettingsScreen(
    modifier: Modifier = Modifier,
    peripheral: DiscoveredPeripheral,
    viewModel: PeripheralViewModel,
    navigateUp: () -> Unit,
    onResetClicked: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier)
            PeripheralSettingsScreen(
                peripheral = peripheral,
                viewModel = viewModel,
            )
            Button(
                onClick = { showDialog = true }
            ) {
                Text(stringResource(R.string.reset_button))
            }
            viewModel.firmwareVersion.value?.let {
                Text(stringResource(R.string.peripheral_settings_text_firmware_version) + it)
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
        }
        PeripheralTopBar(
            title = stringResource(R.string.peripheral_settings_screen_title),
            navigateUp = navigateUp,
            openPeripheralSettings = { },
            showActions = false
        )
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
    peripheral: DiscoveredPeripheral,
    viewModel: PeripheralViewModel,
) {
    when (peripheral.type) {
        PeripheralProfileEnum.FL_CLASSIC -> FLClassicSettingsScreen(viewModel as FLClassicViewModel)
        PeripheralProfileEnum.FL_MINI    -> FLClassicSettingsScreen(viewModel as FLClassicViewModel)
        PeripheralProfileEnum.SL_BASE    -> SLBaseSettingsScreen(viewModel as SLBaseViewModel)
        else -> { Text("HZ") }
    }
}