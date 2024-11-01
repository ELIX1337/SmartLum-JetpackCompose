package com.example.smartlumnew.ui.peripheral

import android.graphics.Color
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.R
import com.example.smartlumnew.models.data.AnimationSettings
import com.example.smartlumnew.models.data.peripheralData.FlClassicAnimations
import com.example.smartlumnew.models.data.peripheralData.FlClassicAnimationDirections
import com.example.smartlumnew.models.viewModels.FLClassicViewModel
import com.example.smartlumnew.ui.components.*
import kotlinx.coroutines.launch

/**
 * UI для экрана устройства FL-CLASSIC
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FLClassic(
    modifier: Modifier = Modifier,
    viewModel: FLClassicViewModel
) {
    val primaryColor       = viewModel.primaryColor.observeAsState(Color.WHITE)
    val secondaryColor     = viewModel.secondaryColor.observeAsState(Color.WHITE)
    val randomColorState   = viewModel.randomColor.observeAsState(false)
    val animationMode      = viewModel.animationMode.observeAsState(FlClassicAnimations.Wave)
    val animationDirection = viewModel.animationDirection.observeAsState(FlClassicAnimationDirections.FromTop)
    val animationSpeed     = viewModel.animationOnSpeed.observeAsState(0f)
    val animationStep      = viewModel.animationStep.observeAsState(0)

    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope            = rememberCoroutineScope()

    // Костыль (а может гениальная релизация) для динамической смены контента внутри BottomSheet
    val content: @Composable (() -> Unit) = { Text(" ") }
    var sheetContent   by remember { mutableStateOf(content) }
    val sheetAnim: AnimationSpec<Float> = tween(200)

    // Переменная, смена которой анимирует смену контента внутри Crossfade
    // В ней лежит режим выбранной анимации
    var crossfadeState by remember { mutableStateOf(animationMode.value) }
    crossfadeState = animationMode.value

    // Почему мы ее вынесли отдельно от других, а не отправляем дальше во ViewModel и менеджер?
    // Потому-что слайдер почему-то начинает лагать
    var speed by remember { mutableStateOf(animationSpeed.value) }

    speed = animationSpeed.value

    // Используется для ColorPicker и ValuePicker (как на iOS)
    if (!bottomSheetState.isVisible) sheetContent = content
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                sheetContent()
            }
        }
    ) {
        // Анимируем UI в зависимости от типа выбранной анимации
        Crossfade(
            targetState = crossfadeState,
        ) { state ->
            Column(
                modifier = Modifier
                    .padding(8.dp, 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Перебираем поддерживаемые настройки для выбранной анимации
                // и для каждой рисуем соответствующую ячейку
                state.supportingSettings.forEach {
                    when (it) {
                        AnimationSettings.PrimaryColor -> {
                            ColorCell(stringResource(R.string.color_cell_primary), primaryColor.value) {
                                // Контент для появляющегося снизу меню
                                sheetContent = {
                                    ColorPicker(
                                        initColor = colorToHSV(primaryColor.value)
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
                        }
                        AnimationSettings.SecondaryColor -> {
                            ColorCell(stringResource(R.string.color_cell_secondary), secondaryColor.value) {
                                // Контент для появляющегося снизу меню
                                sheetContent = {
                                    ColorPicker(
                                        initColor = colorToHSV(secondaryColor.value)
                                    ) { color ->
                                        viewModel.setSecondaryColor(color)
                                    }

                                }
                                scope.launch {
                                    bottomSheetState.animateTo(
                                        ModalBottomSheetValue.Expanded,
                                        sheetAnim
                                    )
                                }
                            }
                        }
                        AnimationSettings.RandomColor -> {
                            SwitchCell(
                                title = stringResource(R.string.switch_cell_random_color) ,
                                value = randomColorState.value
                            ) { state ->
                                viewModel.setRandomColor(state)
                            }
                        }
                        AnimationSettings.Speed -> {
                            SliderCell(
                                title = stringResource(R.string.slider_cell_speed),
                                value = speed,
                                valueRange = 0f..30f,
                                leftIcon = { Icon(Icons.Rounded.FastRewind, "Slow animation") },
                                rightIcon = { Icon(Icons.Rounded.FastForward, "Fast animation") }
                            ) { value ->
                                speed = value
                                viewModel.setAnimationOnSpeed(value)
                            }
                        }
                        AnimationSettings.Direction -> {
                            ValuePickerCell(
                                stringResource(R.string.picker_cell_direction),
                                stringResource(animationDirection.value.elementNameStringID)
                            ) {
                                // Контент для появляющегося снизу меню
                                sheetContent = {
                                    ValuePicker(
                                        items = FlClassicAnimationDirections.values().asList(),
                                        selected = animationDirection.value
                                    ) { selection ->
                                        viewModel.setAnimationDirection(selection as FlClassicAnimationDirections)
                                    }
                                }
                                scope.launch {
                                    bottomSheetState.animateTo(
                                        ModalBottomSheetValue.Expanded,
                                        sheetAnim
                                    )
                                }
                            }
                        }
                        AnimationSettings.Step -> {
                            StepperCell(
                                stringResource(R.string.stepper_cell_step),
                                animationStep.value,
                                1,
                                20
                            ) { step ->
                                viewModel.setAnimationStep(step)
                            }
                        }
                    }
                }

                // Ячейка выбора анимации, отдельно
                ValuePickerCell(
                    stringResource(R.string.picker_cell_animations),
                    stringResource(animationMode.value.elementNameStringID)
                ) {
                    // Контент для появляющегося снизу меню
                    sheetContent = {
                        ValuePicker(
                            items = FlClassicAnimations.values().asList(),
                            selected = animationMode.value
                        ) { selection ->
                            viewModel.setAnimationMode(selection as FlClassicAnimations)
                        }
                    }
                    scope.launch {
                        bottomSheetState.animateTo(
                            ModalBottomSheetValue.Expanded,
                            sheetAnim
                        )
                    }
                }
            }
        }
    }
}

/**
 * Не имеет экрана расширенных настроек
 */
@Composable
fun FLClassicSettingsScreen(
    viewModel: FLClassicViewModel
) {

}