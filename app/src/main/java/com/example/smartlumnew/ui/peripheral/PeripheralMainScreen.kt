package com.example.smartlumnew.ui.peripheral

import android.app.Application
import android.util.Log
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
import com.example.smartlumnew.models.data.PeripheralProfileEnum
import com.example.smartlumnew.models.data.PeripheralError
import com.example.smartlumnew.models.viewModels.*
import com.example.smartlumnew.ui.components.Cell
import com.example.smartlumnew.ui.components.ConnectingScreen
import com.example.smartlumnew.ui.components.TransparentTopBar

/**
 * Обертка для экранов устройств
 */
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
    // Делаем коннект
    viewModel.connect(peripheral)

    // Контейнер для экрана конкретного устройства
    PeripheralScreen(
        modifier = modifier.fillMaxSize(),
        viewModel = viewModel,
        peripheral = peripheral
    )

    // Верхнее меню
    // Иконка для перехода к расширенным настройкам появляется в зависимости от их наличия
    PeripheralTopBar(
        title = stringResource(peripheral.type.peripheralName),
        navigateUp = {
            viewModel.disconnect()
            navigateUp() },
        openPeripheralSettings = { openPeripheralSettings(peripheral, viewModel) },
        // Рисуем или не рисуем иконку
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
    // Если устройство еще не подключилось - показываем экран загрузки
    // Иначе экран конкретного устройства
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

// Устройство подключилось, показываем этот Composable
@Composable
private fun PeripheralScreen(
    modifier: Modifier = Modifier,
    viewModel: PeripheralViewModel,
    peripheralType: PeripheralProfileEnum,
) {
    val isInitialized   by viewModel.isInitialized.observeAsState()
    val error           by viewModel.error.observeAsState()
    var showErrorDialog by remember { mutableStateOf(false) }

    Log.e("TAG", "PeripheralScreen: INIT $isInitialized" )
    Column(modifier = modifier) {
        // Проверяем состояние первичной настройки (инициализации)
        // Если настроено, то показываем экран устройства,
        // иначе показываем экран настройки устройства.
        // Также здесь мы рисуем в самом верху ячейку об ошибке (если есть), при нажании откроет подробности (диалог)
        error?.let { PeripheralErrorCell { showErrorDialog = true } }
        if (isInitialized != false) {
            PeripheralReadyScreen(modifier, peripheralType, viewModel)
        } else if (isInitialized == false) {
            PeripheralSetupScreen(modifier, peripheralType, viewModel)
        }
    }

    // Диалоговое окно с подробностями об ошибке
    // Показывается когда было нажатие по кнопке "Подробнее"
    error?.let {
        PeripheralErrorDialog(
            error = it,
            isOpen = showErrorDialog) {
            showErrorDialog = false
        }
    }
}

// Здесь уже конкретные экраны устройств
// Сделано топорно через switch-case, но это был самый быстрый и простой способ,
// Очевидно, что в будущем от этого нужно отойти
@Composable
fun PeripheralReadyScreen(
    modifier: Modifier = Modifier,
    peripheralType: PeripheralProfileEnum,
    viewModel: PeripheralViewModel
) {
    // Показываем соответствующий экран в зависимости от устройства
    when (peripheralType) {
        PeripheralProfileEnum.FL_CLASSIC  -> FLClassic(modifier, viewModel as FLClassicViewModel)
        PeripheralProfileEnum.FL_MINI     -> FLClassic(modifier, viewModel as FLClassicViewModel)
        PeripheralProfileEnum.SL_BASE     -> SLBaseMainScreen(viewModel as SLBaseViewModel)
        PeripheralProfileEnum.SL_STANDART -> SLStandartMainScreen(viewModel as SLProStandartViewModel)
        PeripheralProfileEnum.SL_PRO      -> SLProMainScreen(viewModel as SLProStandartViewModel)
        PeripheralProfileEnum.UNKNOWN     -> {
            Log.e("TAG", "PeripheralReadyScreen: UNKNOWN DEVICE" )
            Text("Unknown device")
        }
    }
}

// Экран первичной настройки устройств (инициализации)
@Composable
fun PeripheralSetupScreen(
    modifier: Modifier = Modifier,
    peripheralType: PeripheralProfileEnum,
    viewModel: PeripheralViewModel,
) {
    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 12.dp),
            text = stringResource(R.string.peripheral_setup_screen_header),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center
        )
        PeripheralSetupScreen(
            peripheralType = peripheralType,
            viewModel = viewModel,
        )
    }
}

// Конкретные экраны для первичной настройки
// Так же все топорно через switch-case
@Composable
private fun PeripheralSetupScreen(
    peripheralType: PeripheralProfileEnum,
    viewModel: PeripheralViewModel,
) {
    when (peripheralType) {
        PeripheralProfileEnum.FL_CLASSIC -> FLClassicSettingsScreen(viewModel as FLClassicViewModel)
        PeripheralProfileEnum.FL_MINI    -> FLClassicSettingsScreen(viewModel as FLClassicViewModel)
        PeripheralProfileEnum.SL_BASE    -> SLBaseSetupScreen(viewModel as SLBaseViewModel)
        PeripheralProfileEnum.SL_STANDART -> SLStandartSetupScreen(viewModel as SLProStandartViewModel)
        PeripheralProfileEnum.SL_PRO     -> SLProSetupScreen(viewModel as SLProStandartViewModel)
        PeripheralProfileEnum.UNKNOWN    -> Text("Unknown device")
    }
}

// Верхнее меню
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

// Ячейка с ошибкой
// В отличии от iOS не показывает код ошибки
// Просто имеет кнопку "Подробнее"
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

// Диалоговое окно в котором подробная информация об ошибке на устройстве
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

@Composable
fun PeripheralInitAlertDialog(
    isOpen: Boolean,
    dismiss: () -> Unit
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = dismiss,
            title = {
                Text(
                    text = "Please specify all the fields",
                    fontWeight = FontWeight.SemiBold
                )
            },
            confirmButton = {
                Button(dismiss) {
                    Text(stringResource(R.string.alert_dialog_peripheral_error_dismiss_button))
                }
            }
        )
    }
}