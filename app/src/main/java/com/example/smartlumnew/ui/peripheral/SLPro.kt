package com.example.smartlumnew.ui.peripheral

import android.graphics.Color
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.R
import com.example.smartlumnew.models.data.*
import com.example.smartlumnew.models.data.peripheralData.SlProAdaptiveModes
import com.example.smartlumnew.models.data.peripheralData.SlProAnimations
import com.example.smartlumnew.models.data.peripheralData.SlProStandartControllerType
import com.example.smartlumnew.models.data.peripheralData.SlProStairsWorkModes
import com.example.smartlumnew.models.viewModels.SLProStandartViewModel
import com.example.smartlumnew.ui.components.*
import kotlinx.coroutines.launch

/**
 * UI для экрана устройства SL-PRO
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SLProMainScreen(
    viewModel: SLProStandartViewModel
) {
    val primaryColor by viewModel.primaryColor.observeAsState(Color.WHITE)
    val randomColor by viewModel.randomColor.observeAsState(false)
    val ledState by viewModel.ledState.observeAsState(false)
    var ledBrightness by remember { mutableStateOf(viewModel.ledBrightness.value ?: 0f) }
    val ledTimeout by viewModel.ledTimeout.observeAsState(0)
    val animationMode by viewModel.animationMode.observeAsState(SlProAnimations.Sharp)
    var animationSpeed by remember { mutableStateOf(viewModel.animationOnSpeed.value ?: 0f) }

    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope            = rememberCoroutineScope()
    val content: @Composable (() -> Unit) = { Text(" ") }
    var sheetContent   by remember { mutableStateOf(content) }
    val sheetAnim: AnimationSpec<Float> = tween(200)

    if (!bottomSheetState.isVisible) sheetContent = content
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                sheetContent()
            }
        }
    ) {
        Column(
            modifier = Modifier
                //.verticalScroll(rememberScrollState())
                .padding(8.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (viewModel.controllerType.observeAsState().value != SlProStandartControllerType.Default) {
                ColorCell(title = stringResource(R.string.color_cell_title), color = primaryColor) {
                    sheetContent = {
                        ColorPicker(
                            initColor = colorToHSV(primaryColor)
                        ) { color ->
                            viewModel.setPrimaryColor(color)
                        }
                    }
                    scope.launch {
                        bottomSheetState.animateTo(
                            ModalBottomSheetValue.Expanded,
                            sheetAnim
                        )
                    }
                }
                SwitchCell(title = stringResource(R.string.random_color_cell_title), value = randomColor) {
                    viewModel.setRandomColor(it)
                }
            }
            SwitchCell(title = stringResource(R.string.led_state_cell_title), value = ledState) {
                viewModel.setLedState(it)
            }
            if (viewModel.adaptiveBrightness.observeAsState().value == SlProAdaptiveModes.Off) {
                SliderCell(
                    title = stringResource(R.string.brightness_cell_title),
                    value = ledBrightness,
                    valueRange = PeripheralData.SLProMinLedBrightness.toFloat()..PeripheralData.SLProMaxLedBrightness.toFloat()) {
                    ledBrightness = it
                    viewModel.setLedBrightness(it)
                }
            }
            StepperCell(
                title = stringResource(R.string.led_timeout_cell_title),
                value = ledTimeout,
                minValue = PeripheralData.SLProMinLedTimeout,
                maxValue = PeripheralData.SLProMaxLedTimeout
                ) {
                viewModel.setLedTimeout(it)
            }
            ValuePickerCell(stringResource(R.string.animation_mode_cell_title), stringResource(id = animationMode.elementNameStringID)) {
                sheetContent = {
                    ValuePicker(
                        items = SlProAnimations.values().asList(),
                        selected = animationMode
                    ) { selection ->
                        viewModel.setAnimationMode(selection as SlProAnimations)
                    }
                }
                scope.launch {
                    bottomSheetState.animateTo(
                        ModalBottomSheetValue.Expanded,
                        sheetAnim
                    )
                }
            }
            SliderCell(
                title = stringResource(R.string.animation_speed_cell_title),
                value = animationSpeed,
                valueRange = PeripheralData.SLProMinAnimationSpeed.toFloat()..PeripheralData.SLProMaxAnimationSpeed.toFloat()
            ) {
                animationSpeed = it
                viewModel.setAnimationOnSpeed(it)
            }
        }
    }

}

/**
 * Экран первичной настройки (инициализации) устройства SL-PRO
 */
@Composable
fun SLProSetupScreen(
    viewModel: SLProStandartViewModel
) {
    var topTriggerDistance by remember { mutableStateOf(viewModel.topTriggerDistance.value ?: 0f) }
    var botTriggerDistance by remember { mutableStateOf(viewModel.botTriggerDistance.value ?: 0f) }
    var stepsCount by remember { mutableStateOf(viewModel.stepsCount.value ?: 0) }
    var topSensorCount by remember { mutableStateOf(viewModel.topSensorCount.value ?:1) }
    var botSensorCount by remember { mutableStateOf(viewModel.botSensorCount.value ?:1) }

    var showInitAlert by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StepperCell(
            title = stringResource(R.string.steps_count_cell_title),
            value = stepsCount,
            minValue = PeripheralData.SLProMinStepsCount,
            maxValue = PeripheralData.SLProMaxStepsCount
        ) {
            stepsCount = it
            viewModel.initStepsCount(it)
        }

        StepperCell(
            title = stringResource(id = R.string.top_sensor_count_cell_title),
            value = topSensorCount,
            minValue = PeripheralData.SLProMinSensorCount,
            maxValue = PeripheralData.SLProMaxSensorCount
        ) {
            topSensorCount = it
            viewModel.initTopSensorCount(it)
        }

        StepperCell(
            title = stringResource(id = R.string.bot_sensor_count_cell_title),
            value = botSensorCount,
            minValue = PeripheralData.SLProMinSensorCount,
            maxValue = PeripheralData.SLProMaxSensorCount
        ) {
            botSensorCount = it
            viewModel.initBotSensorCount(it)
        }

        SliderCell(
            title = stringResource(R.string.title_top_sensor_trigger_distance),
            value = topTriggerDistance,
            valueRange = PeripheralData.SLProMinSensorDistance.toFloat()..PeripheralData.SLProMaxSensorDistance.toFloat(),
            additionalContent = { Text(topTriggerDistance.toInt().toString() + stringResource(R.string.peripheral_sensor_distance_current_distance)) },
            onValueChanged = {
                topTriggerDistance = it
                viewModel.initTopSensorTriggerDistance(it)
            }
        )

        SliderCell(
            title = stringResource(R.string.title_bot_sensor_trigger_distance),
            value = botTriggerDistance,
            valueRange = PeripheralData.SLProMinSensorDistance.toFloat()..PeripheralData.SLProMaxSensorDistance.toFloat(),
            additionalContent = { Text(botTriggerDistance.toInt().toString() + stringResource(R.string.peripheral_sensor_distance_current_distance) ) },
            onValueChanged = {
                botTriggerDistance = it
                viewModel.initBotSensorTriggerDistance(it)
            }
        )

        Button(
            onClick = {
                showInitAlert = viewModel.commit()
            }
        ) {
            Text(stringResource(R.string.button_commit))
        }

        // Диалоговое окно с подробностями об ошибке
        // Показывается когда было нажатие по кнопке "Подробнее"

        if (!showInitAlert) {
            PeripheralInitAlertDialog(
                isOpen = showInitAlert
            ) {
                showInitAlert = true
            }
        }
    }
}

/**
 * Экран расширенных настроек устройства SL-PRO
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SLProSettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SLProStandartViewModel,
    resetSettings: () -> Unit,
) {
    val controllerType by viewModel.controllerType.observeAsState(SlProStandartControllerType.Default)
    val adaptiveBrightness by viewModel.adaptiveBrightness.observeAsState(SlProAdaptiveModes.Off)
    val stairsWorkMode by viewModel.stairsWorkMode.observeAsState(SlProStairsWorkModes.BySensors)
    val stepsCount by viewModel.stepsCount.observeAsState(24)
    val topSensorCount by viewModel.topSensorCount.observeAsState(1)
    val botSensorCount by viewModel.botSensorCount.observeAsState(1)
    val standbyState by viewModel.standbyState.observeAsState(false)
    var standbyBrightness by remember { mutableStateOf(viewModel.standbyBrightness.value ?: 0f) }
    val standbyTopCount by viewModel.standbyTopCount.observeAsState(1)
    val standbyBotCount by viewModel.standbyBotCount.observeAsState(1)
    var topTriggerDistance by remember { mutableStateOf(viewModel.topTriggerDistance.value ?: 20f) }
    var botTriggerDistance by remember { mutableStateOf(viewModel.botTriggerDistance.value ?: 20f) }
    var topTriggerLightness by remember { mutableStateOf(viewModel.topTriggerLightness.value ?: 0f) }
    var botTriggerLightness by remember { mutableStateOf(viewModel.botTriggerLightness.value ?: 0f) }
    val topCurrentLightness by viewModel.topCurrentLightness.observeAsState(0)
    val botCurrentLightness by viewModel.botCurrentLightness.observeAsState(0)

    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope            = rememberCoroutineScope()
    val content: @Composable (() -> Unit) = { Text("NULL") }
    var sheetContent   by remember { mutableStateOf(content) }
    val sheetAnim: AnimationSpec<Float> = tween(200)

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                sheetContent()
            }
        },
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp, 6.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding())
            ValuePickerCell(
                title = stringResource(id = R.string.controller_type_cell_title),
                value = controllerType.name,
                showIcon = false
            )
            ValuePickerCell(
                stringResource(id = R.string.adaptive_brightness_cell_title),
                stringResource(adaptiveBrightness.elementNameStringID)
            ) {
                sheetContent = {
                    ValuePicker(
                        items = SlProAdaptiveModes.values().asList(),
                        selected = adaptiveBrightness
                    ) { selection ->
                        viewModel.setAdaptiveMode(selection as SlProAdaptiveModes)
                    }
                }
                scope.launch {
                    bottomSheetState.animateTo(
                        ModalBottomSheetValue.Expanded,
                        sheetAnim
                    )
                }
            }
            ValuePickerCell(
                stringResource(id = R.string.stairs_work_mode_cell_title),
                stringResource(stairsWorkMode.elementNameStringID)
            ) {
                sheetContent = {
                    ValuePicker(
                        items = SlProStairsWorkModes.values().asList(),
                        selected = stairsWorkMode
                    ) { selection ->
                        viewModel.setStairsWorkMode(selection as SlProStairsWorkModes)
                    }
                }
                scope.launch {
                    bottomSheetState.animateTo(
                        ModalBottomSheetValue.Expanded,
                        sheetAnim
                    )
                }
            }
            StepperCell(
                title = stringResource(id = R.string.steps_count_cell_title),
                value = stepsCount,
                minValue = PeripheralData.SLProMinStepsCount,
                maxValue = PeripheralData.SLProMaxStepsCount,
                ) {
                viewModel.setStepsCount(it)
            }
            StepperCell(
                title = stringResource(id = R.string.top_sensor_count_cell_title),
                value = topSensorCount,
                minValue = PeripheralData.SLProMinSensorCount,
                maxValue = PeripheralData.SLProMaxSensorCount
            ) {
                viewModel.setTopSensorsCount(it)
            }
            StepperCell(
                title = stringResource(id = R.string.bot_sensor_count_cell_title),
                value = botSensorCount,
                minValue = PeripheralData.SLProMinSensorCount,
                maxValue = PeripheralData.SLProMaxSensorCount
            ) {
                viewModel.setBotSensorsCount(it)
            }
            SwitchCell(
                title = stringResource(id = R.string.standby_brightness_state_cell_title),
                value = standbyState
            ) {
                viewModel.setStandbyState(it)
            }
            if (adaptiveBrightness == SlProAdaptiveModes.Off) {
                SliderCell(
                    title = stringResource(id = R.string.standby_brightness_value_cell_title),
                    value = standbyBrightness,
                    valueRange = PeripheralData.SLProMinLedBrightness.toFloat()..PeripheralData.SLProMaxLedBrightness.toFloat()
                ) {
                    standbyBrightness = it
                    viewModel.setStandbyBrightness(it)
                }
            }
            StepperCell(
                title = stringResource(id = R.string.standby_brightness_top_steps_count_cell_title),
                value = standbyTopCount,
                minValue = 1,
                maxValue = PeripheralData.SLProMaxStepsCount / 2
            ) {
                viewModel.setStandbyTopCount(it)
            }
            StepperCell(
                title = stringResource(id = R.string.standby_brightness_bot_steps_count_cell_title),
                value = standbyBotCount,
                minValue = 1,
                maxValue = PeripheralData.SLProMaxStepsCount / 2
            ) {
                viewModel.setStandbyBotCount(it)
            }
            SliderCell(
                title = stringResource(R.string.title_top_sensor_trigger_distance),
                value = topTriggerDistance,
                valueRange = PeripheralData.SLProMinSensorDistance.toFloat()..PeripheralData.SLProMaxSensorDistance.toFloat(),
                additionalContent = {
                    Text(topTriggerDistance.toInt().toString() + stringResource(R.string.peripheral_sensor_distance_current_distance))
                },
                onValueChanged = {
                    topTriggerDistance = it
                    viewModel.setTopSensorTriggerDistance(it)
                }
            )
            SliderCell(
                title = stringResource(R.string.title_bot_sensor_trigger_distance),
                value = botTriggerDistance,
                valueRange = PeripheralData.SLProMinSensorDistance.toFloat()..PeripheralData.SLProMaxSensorDistance.toFloat(),
                additionalContent = {
                    Text(botTriggerDistance.toInt().toString() + stringResource(R.string.peripheral_sensor_distance_current_distance))
                },
                onValueChanged = {
                    botTriggerDistance = it
                    viewModel.setBotSensorTriggerDistance(it)
                }
            )
            SliderCell(
                title = stringResource(id = R.string.top_trigger_lightness_cell_title),
                value = topTriggerLightness,
                valueRange = PeripheralData.SLProMinSensorLightness.toFloat()..PeripheralData.SLProMaxSensorLightness.toFloat(),
                additionalContent = {
                    Text(topTriggerLightness.toInt().toString())
                },
            ) {
                topTriggerLightness = it
                viewModel.setTopSensorTriggerLightness(it)
            }
            SliderCell(
                title = stringResource(id = R.string.bot_trigger_lightness_cell_title),
                value = botTriggerLightness,
                valueRange = PeripheralData.SLProMinSensorLightness.toFloat()..PeripheralData.SLProMaxSensorLightness.toFloat(),
                additionalContent = {
                    Text(botTriggerLightness.toInt().toString())
                },
            ) {
                botTriggerLightness = it
                viewModel.setBotSensorTriggerLightness(it)
            }
            // Не нужно
//            ValuePickerCell(
//                title = stringResource(R.string.top_current_distance_cell_title),
//                value = topCurrentDistance.toString()
//            )
//            ValuePickerCell(
//                title = stringResource(R.string.bot_current_distance_cell_title),
//                value = botCurrentDistance.toString()
//            )
            ValuePickerCell(
                title = stringResource(id = R.string.top_current_lightness_cell_title),
                value = topCurrentLightness.toString()
            )
            ValuePickerCell(
                title = stringResource(id = R.string.bot_current_lightness_cell_title),
                value = botCurrentLightness.toString()
            )
            Button(onClick = resetSettings) {
                Text(stringResource(R.string.reset_button))
            }
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }

}