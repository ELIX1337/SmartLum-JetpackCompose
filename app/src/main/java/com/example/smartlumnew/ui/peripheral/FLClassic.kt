package com.example.smartlumnew.ui.peripheral

import android.graphics.Color
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import com.example.smartlumnew.models.data.FlClassicAnimations
import com.example.smartlumnew.models.data.PeripheralAnimationDirections
import com.example.smartlumnew.models.viewModels.FLClassicViewModel
import com.example.smartlumnew.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FLClassic(
    //modifier: Modifier = Modifier,
    viewModel: FLClassicViewModel
) {
    val primaryColor       = viewModel.primaryColor.observeAsState(Color.WHITE)
    val secondaryColor     = viewModel.secondaryColor.observeAsState(Color.WHITE)
    val randomColorState   = viewModel.randomColor.observeAsState(false)
    val animationMode      = viewModel.animationMode.observeAsState(FlClassicAnimations.Wave)
    val animationDirection = viewModel.animationDirection.observeAsState(PeripheralAnimationDirections.FromTop)
    val animationSpeed     = viewModel.animationOnSpeed.observeAsState(0f)
    val animationStep      = viewModel.animationStep.observeAsState(0)

    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope            = rememberCoroutineScope()
    val content: @Composable (() -> Unit) = { Text("NULL") }
    var sheetContent   by remember { mutableStateOf(content) }
    val sheetAnim: AnimationSpec<Float> = tween(200)
    var crossfadeState by remember { mutableStateOf(animationMode.value) }
    crossfadeState = animationMode.value

    var speed by remember { mutableStateOf(animationSpeed.value) }

    speed = animationSpeed.value

    if (!bottomSheetState.isVisible) sheetContent = content
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            sheetContent()
        }
    ) {
        Crossfade(
            targetState = crossfadeState,
        ) { state ->
            Column(
                modifier = Modifier
                    .padding(8.dp, 16.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                state.supportingSettings.forEach {
                    when (it) {
                        AnimationSettings.PrimaryColor -> {
                            ColorCell(stringResource(R.string.color_cell_primary), primaryColor.value) {
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
                            SwitchCell(stringResource(R.string.switch_cell_random_color) ,randomColorState.value) { state ->
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
                                stringResource(animationDirection.value.elementName)
                            ) {
                                sheetContent = {
                                    ValuePicker(
                                        items = PeripheralAnimationDirections.values().asList(),
                                        selected = animationDirection.value
                                    ) { selection ->
                                        viewModel.setAnimationDirection(selection as PeripheralAnimationDirections)
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
                                0,
                                20
                            ) { step ->
                                viewModel.setAnimationStep(step)
                            }
                        }
                    }
                }

                ValuePickerCell(
                    stringResource(R.string.picker_cell_animations),
                    stringResource(animationMode.value.elementName)
                ) {
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

@Composable
fun FLClassicSettingsScreen(
    viewModel: FLClassicViewModel
) {

}