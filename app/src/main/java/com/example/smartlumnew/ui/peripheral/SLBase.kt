package com.example.smartlumnew.ui.peripheral

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.R
import com.example.smartlumnew.models.viewModels.SLBaseViewModel
import com.example.smartlumnew.ui.components.SliderCell
import com.example.smartlumnew.ui.components.StepperCell
import com.example.smartlumnew.ui.components.SwitchCell
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun SLBaseMainScreen(
    //modifier: Modifier = Modifier,
    baseViewModel: SLBaseViewModel
) {
    var ledState by remember { mutableStateOf(baseViewModel.ledState.value ?: false) }
    var brightness by remember { mutableStateOf(baseViewModel.ledBrightness.value ?: 0f) }
    var onSpeed by remember { mutableStateOf(baseViewModel.animationOnSpeed.value ?: 1f) }
    var ledTimeout by remember { mutableStateOf(baseViewModel.ledTimeout.value ?: 0) }

    Column(
        modifier = Modifier
            //.verticalScroll(rememberScrollState())
            .padding(8.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SwitchCell(
            title = stringResource(R.string.switch_cell_led_on),
            value = ledState,
            additionalContent = { Text(stringResource(R.string.sl_base_led_on_cell_description)) }
        ) {
            ledState = it
            baseViewModel.setLedState(it)
        }
        SliderCell(
            stringResource(R.string.slider_cell_led_brightness),
            brightness,
            1f..100f,
            leftIcon = { Icon(Icons.Rounded.BrightnessLow, "Low brightness" ) },
            rightIcon = { Icon(Icons.Rounded.BrightnessHigh, "High brightness") },
        ) {
            brightness = it
            baseViewModel.setLedBrightness(it)
        }
        SliderCell(
            stringResource(R.string.sl_base_cell_led_on_speed),
            onSpeed,
            valueRange = 1f..10f,
            leftIcon = { Icon(Icons.Rounded.FastRewind, "Slow animation") },
            rightIcon = { Icon(Icons.Rounded.FastForward, "Fast animation") }
        ) { speed ->
            onSpeed = speed
            baseViewModel.setAnimationOnSpeed(speed)
        }
        StepperCell(stringResource(R.string.stepper_cell_led_timeout), ledTimeout, 1, 30) {
            ledTimeout = it
            baseViewModel.setLedTimeout(it)
        }

    }
}

@Composable
fun SLBaseSettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SLBaseViewModel,
    resetSettings: () -> Unit,
) {
    var topSensorTriggerDistance by remember { mutableStateOf(viewModel.topSensorTriggerDistance.value ?: 1f) }
    var botSensorTriggerDistance by remember { mutableStateOf(viewModel.botSensorTriggerDistance.value ?: 1f) }
    Column(
        modifier = Modifier
            .padding(12.dp, 6.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SliderCell(
            title = stringResource(R.string.title_top_sensor_trigger_distance),
            value = topSensorTriggerDistance,
            valueRange = 20f..200f,
            additionalContent = { Text(topSensorTriggerDistance.toInt().toString() + stringResource(R.string.peripheral_sensor_distance_current_distance)) },
            onValueChanged = {
                topSensorTriggerDistance = it
                viewModel.setTopSensorTriggerDistance(it)
            }
        )
        SliderCell(
            title = stringResource(R.string.title_bot_sensor_trigger_distance),
            value = botSensorTriggerDistance,
            valueRange = 20f..200f,
            additionalContent = { Text(botSensorTriggerDistance.toInt().toString() + stringResource(R.string.peripheral_sensor_distance_current_distance)) },
            onValueChanged = {
                botSensorTriggerDistance = it
                viewModel.setBotSensorTriggerDistance(it)
            }
        )
        Button(onClick = resetSettings) {
            Text(stringResource(R.string.reset_button))
        }
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
fun SLBaseSetupScreen(
    viewModel: SLBaseViewModel
) {
    var topSensorTriggerDistance by remember { mutableStateOf(viewModel.topSensorTriggerDistance.value ?: 1) }
    var botSensorTriggerDistance by remember { mutableStateOf(viewModel.botSensorTriggerDistance.value ?: 1) }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SliderCell(
            title = stringResource(R.string.title_top_sensor_trigger_distance),
            value = topSensorTriggerDistance.toFloat(),
            valueRange = 20f..200f,
            additionalContent = { Text(topSensorTriggerDistance.toInt().toString() + stringResource(R.string.peripheral_sensor_distance_current_distance)) },
            onValueChanged = {
                topSensorTriggerDistance = it.toInt()
                viewModel.initTopSensorTriggerDistance(it)
            }
        )
        SliderCell(
            title = stringResource(R.string.title_bot_sensor_trigger_distance),
            value = botSensorTriggerDistance.toFloat(),
            valueRange = 20f..200f,
            additionalContent = { Text(botSensorTriggerDistance.toInt().toString() + stringResource(R.string.peripheral_sensor_distance_current_distance)) },
            onValueChanged = {
                botSensorTriggerDistance = it.toInt()
                viewModel.initBotSensorTriggerDistance(it)
            }
        )
        Button(onClick = { viewModel.commit() }) {
            Text(stringResource(R.string.button_commit))
        }
    }
}