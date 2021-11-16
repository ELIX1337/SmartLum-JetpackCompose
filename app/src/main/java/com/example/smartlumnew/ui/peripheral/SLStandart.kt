package com.example.smartlumnew.ui.peripheral

import android.graphics.Color
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.R
import com.example.smartlumnew.models.data.SlStandartAnimations
import com.example.smartlumnew.models.viewModels.SLStandartViewModel
import com.example.smartlumnew.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SLStandartMainScreen(
    viewModel: SLStandartViewModel
) {
    var demoMode by remember { mutableStateOf(viewModel.demoMode.value ?: 0) }
    var primaryColor by remember { mutableStateOf(viewModel.primaryColor.value ?: Color.WHITE) }
    var randomColor by remember { mutableStateOf(viewModel.randomColor.value ?: false) }
    var ledState by remember { mutableStateOf(viewModel.ledState.value ?: false) }
    var ledBrightness by remember { mutableStateOf(viewModel.ledBrightness.value ?: 0f) }
    var ledTimeout by remember { mutableStateOf(viewModel.ledTimeout.value ?: 0) }
    var ledType by remember { mutableStateOf(viewModel.ledType.value ?: 0) }
    var animationMode by remember { mutableStateOf(viewModel.animationMode.value ?: SlStandartAnimations.Wave) }
    var animationSpeed by remember { mutableStateOf(viewModel.animationOnSpeed.value ?: 0f) }

    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope            = rememberCoroutineScope()
    val content: @Composable (() -> Unit) = { Text("NULL") }
    var sheetContent   by remember { mutableStateOf(content) }
    val sheetAnim: AnimationSpec<Float> = tween(200)
    var crossfadeState by remember { mutableStateOf(animationMode) }
    crossfadeState = animationMode

    var speed by remember { mutableStateOf(animationSpeed) }
    speed = animationSpeed

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            sheetContent()
        }
    ) {
        Column {
            ColorCell(title = "Color", color = primaryColor) {
                sheetContent = {
                    ColorPicker(
                        initColor = colorToHSV(primaryColor)
                    ) { color ->
                        viewModel.setPrimaryColor(color)
                    }
                }
            }
            SwitchCell(title = "Random color", initValue = randomColor) {
                randomColor = it
                viewModel.setRandomColor(it)
            }
            SwitchCell(title = "LED state", initValue = ledState) {
                ledState = it
                viewModel.setLedState(it)
            }
            SliderCell(title = "Brightness", value = ledBrightness, valueRange = 0f..100f) {
                ledBrightness = it
                viewModel.setLedBrightness(it)
            }
            StepperCell(title = "LED timeout", initValue = ledTimeout) {
                ledTimeout = it
                viewModel.setLedTimeout(it)
            }
            ValuePickerCell("Animation mode", stringResource(id = animationMode.elementName)) {
                sheetContent = {
                    ValuePicker(
                        items = SlStandartAnimations.values().asList(),
                        selected = animationMode
                    ) { selection ->
                        viewModel.setAnimationMode(selection as SlStandartAnimations)
                    }
                }
                scope.launch {
                    bottomSheetState.animateTo(
                        ModalBottomSheetValue.Expanded,
                        sheetAnim
                    )
                }
            }
            SliderCell(title = "Animation speed", value = animationSpeed, valueRange = 0f..30f) {
                animationSpeed = it
                viewModel.setAnimationOnSpeed(it)
            }
        }
    }

}

@Composable
fun SLStandartSetupScreen(
    viewModel: SLStandartViewModel
) {
    var topTriggerDistance by remember { mutableStateOf(viewModel.topTriggerDistance.value ?: 0f) }
    var botTriggerDistance by remember { mutableStateOf(viewModel.botTriggerDistance.value ?: 0f) }
    var topTriggerLightness by remember { mutableStateOf(viewModel.topTriggerLightness.value ?: 0) }
    var botTriggerLightness by remember { mutableStateOf(viewModel.botTriggerLightness.value ?: 0) }
    var topCurrentDistance by remember { mutableStateOf(viewModel.topCurrentDistance.value ?: 0) }
    var botCurrentDistance by remember { mutableStateOf(viewModel.botCurrentDistance.value ?: 0) }
    var topCurrentLightness by remember { mutableStateOf(viewModel.topCurrentLightness.value ?: 0) }
    var botCurrentLightness by remember { mutableStateOf(viewModel.botCurrentLightness.value ?: 0) }
    var stepsCount by remember { mutableStateOf(viewModel.stepsCount.value ?: 0) }
    var standbyState by remember { mutableStateOf(viewModel.standbyState.value ?: false) }
    var standbyTopCount by remember { mutableStateOf(viewModel.standbyTopCount.value ?: 0) }
    var standbyBotCount by remember { mutableStateOf(viewModel.standbyBotCount.value ?: 0) }
    var standbyBrightness by remember { mutableStateOf(viewModel.standbyBrightness.value ?: 0f) }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StepperCell(title = "Steps count", initValue = stepsCount) {
            stepsCount = it
            viewModel.setStepsCount(it)
        }
        SwitchCell(title = "Standby lightness", initValue = standbyState) {
            standbyState = it
            viewModel.setStandbyState(it)
        }
        SliderCell(title = "Standby brightness", value = standbyBrightness, valueRange = 0f..100f) {
            standbyBrightness = it
            viewModel.setStandbyBrightness(it)
        }
        StepperCell(title = "Top steps count", initValue = standbyTopCount) {
            standbyTopCount = it
            viewModel.setStandbyTopCount(it)
        }
        StepperCell(title = "Bot steps count", initValue = standbyBotCount) {
            standbyBotCount = it
            viewModel.setStandbyBotCount(it)
        }
        SliderCell(
            title = stringResource(R.string.title_top_sensor_trigger_distance),
            value = topTriggerDistance,
            valueRange = 1f..200f,
            additionalContent = { Text(stringResource(R.string.peripheral_sensor_distance_current_distance) + topTriggerDistance.toInt()) },
            onValueChanged = {
                topTriggerDistance = it
                viewModel.initTopSensorTriggerDistance(it)
            }
        )
        SliderCell(
            title = stringResource(R.string.title_bot_sensor_trigger_distance),
            value = botTriggerDistance,
            valueRange = 1f..200f,
            additionalContent = { Text(stringResource(R.string.peripheral_sensor_distance_current_distance) + botTriggerDistance.toInt()) },
            onValueChanged = {
                botTriggerDistance = it
                viewModel.initBotSensorTriggerDistance(it)
            }
        )
        Button(onClick = { viewModel.commit() }) {
            Text(stringResource(R.string.button_commit))
        }
    }
}