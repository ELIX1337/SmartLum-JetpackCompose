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
            //.padding(12.dp, 0.dp)
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
            Text("Firmware version - ${viewModel.firmwareVersion.value}")
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
            title = "Device settings",
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
                Text("Are you sure to reset?")
            },
            text = {
                Text("This action will set factory settings")
            },
            confirmButton = {
                Button(confirm) {
                    Text("Reset")
                }
            },
            dismissButton = {
                Button(dismiss) {
                    Text("Cancel")
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