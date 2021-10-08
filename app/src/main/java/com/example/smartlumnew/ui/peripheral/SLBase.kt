package com.example.smartlumnew.ui.peripheral

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrightnessHigh
import androidx.compose.material.icons.rounded.BrightnessLow
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.R
import com.example.smartlumnew.models.viewModels.SLBaseViewModel
import com.example.smartlumnew.ui.components.SliderCell
import com.example.smartlumnew.ui.components.StepperCell
import com.example.smartlumnew.ui.components.SwitchCell

@Composable
fun SLBaseMainScreen(
    modifier: Modifier = Modifier,
    baseViewModel: SLBaseViewModel
) {
    var ledState by remember { mutableStateOf(baseViewModel.ledState.value ?: false) }
    var brightness by remember { mutableStateOf(baseViewModel.ledBrightness.value ?: 0f) }
    var onSpeed by remember { mutableStateOf(baseViewModel.animationOnSpeed.value ?: 1f) }
    var ledTimeout by remember { mutableStateOf(baseViewModel.ledTimeout.value ?: 0) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SwitchCell(
            title = stringResource(R.string.switch_cell_led_on),
            initValue = ledState,
            additionalContent = { Row { Text("This option will enable after disconnection") } }
        ) {
            ledState = it
            baseViewModel.setLedState(it)
        }
        SliderCell(
            stringResource(R.string.slider_cell_led_brightness),
            brightness,
            0f..100f,
            leftIcon = { Icon(Icons.Rounded.BrightnessLow, "Low brightness" ) },
            rightIcon = { Icon(Icons.Rounded.BrightnessHigh, "High brightness") },
        ) {
            brightness = it
            baseViewModel.setLedBrightness(it)
        }
        SliderCell(
            stringResource(R.string.slider_cell_led_on_speed),
            onSpeed,
            valueRange = 0f..10f,
            leftIcon = { Icon(Icons.Rounded.FastRewind, "Slow animation") },
            rightIcon = { Icon(Icons.Rounded.FastForward, "Fast animation") }
        ) { speed ->
            onSpeed = speed
            baseViewModel.setAnimationOnSpeed(speed)
        }
        StepperCell(stringResource(R.string.stepper_cell_led_timeout), ledTimeout, 0, 10) {
            ledTimeout = it
            baseViewModel.setLedTimeout(it)
        }

    }
}

@Composable
fun SLBaseSettingsScreen(
    viewModel: SLBaseViewModel
) {
    var topSensorTriggerDistance by remember { mutableStateOf(viewModel.topSensorTriggerDistance.value ?: 1f) }
    var botSensorTriggerDistance by remember { mutableStateOf(viewModel.botSensorTriggerDistance.value ?: 1f) }
    Column(
        modifier = Modifier.padding(12.dp, 0.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SliderCell(
            title = stringResource(R.string.title_top_sensor_trigger_distance),
            initValue = topSensorTriggerDistance,
            valueRange = 1f..200f,
            additionalContent = { Text("Current value - ${topSensorTriggerDistance.toInt()}") },
            onValueChanged = {
                topSensorTriggerDistance = it
                viewModel.setTopSensorTriggerDistance(it)
            }
        )
        SliderCell(
            title = stringResource(R.string.title_bot_sensor_trigger_distance),
            initValue = botSensorTriggerDistance,
            valueRange = 1f..200f,
            additionalContent = { Text("Current value - ${botSensorTriggerDistance.toInt()}") },
            onValueChanged = {
                botSensorTriggerDistance = it
                viewModel.setBotSensorTriggerDistance(it)
            }
        )
    }
}

@Composable
fun SLBaseSetupScreen(
    viewModel: SLBaseViewModel
) {
    var topSensorTriggerDistance by remember { mutableStateOf(viewModel.topSensorTriggerDistance.value ?: 1) }
    var botSensorTriggerDistance by remember { mutableStateOf(viewModel.botSensorTriggerDistance.value ?: 1) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SliderCell(
            title = stringResource(R.string.title_top_sensor_trigger_distance),
            initValue = topSensorTriggerDistance.toFloat(),
            valueRange = 1f..200f,
            additionalContent = { Text("Current value - $topSensorTriggerDistance") },
            onValueChanged = {
                topSensorTriggerDistance = it.toInt()
                viewModel.setTopSensorTriggerDistance(it)
            }
        )
        SliderCell(
            title = stringResource(R.string.title_bot_sensor_trigger_distance),
            initValue = botSensorTriggerDistance.toFloat(),
            valueRange = 1f..200f,
            additionalContent = { Text("Current value - $botSensorTriggerDistance") },
            onValueChanged = {
                botSensorTriggerDistance = it.toInt()
                viewModel.setBotSensorTriggerDistance(it)
            }
        )
        Button(onClick = { viewModel.commit() }) {
            Text(stringResource(R.string.button_commit))
        }
    }
}