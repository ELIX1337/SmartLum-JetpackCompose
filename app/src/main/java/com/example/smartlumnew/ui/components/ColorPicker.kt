package com.example.smartlumnew.ui.components

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.github.antonpopoff.colorwheel.ColorWheel
import com.github.antonpopoff.colorwheel.gradientseekbar.GradientSeekBar
import com.github.antonpopoff.colorwheel.gradientseekbar.setBlackToColor
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Preview
@Composable
fun ColorPickerPreview(
    //modifier: Modifier = Modifier
) {
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var color by remember { mutableStateOf(android.graphics.Color.WHITE) }
    var brightness by remember { mutableStateOf(100) }
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                ColorPicker(color) {
                    Log.e("TAG", "ON COLOR CHANGED")
                    color = it
                }
                BrightnessSlider(
                    color = color,
                    value = brightness,
                    onBrightnessChanged = {
                        Log.e("TAG", "ON BRIGHTNESS CHANGED")
                        brightness = it
                    }
                )
            }
        }
    ) {
        Column {
            Button(onClick = {
                scope.launch { bottomSheetState.animateTo(ModalBottomSheetValue.Expanded, tween(100))}
            }) {
                Text("Show")
            }

            Card(backgroundColor = Color(color)) { Text("Color") }
            Card() { Text("Brightness - $brightness%") }
        }
    }

}


@Composable
fun ColorPicker(
    initColor: Int = android.graphics.Color.WHITE,
    onColorChanged: (Int) -> Unit
) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        factory = { context ->
            ColorWheel(context).apply {
                this.rgb = android.graphics.Color.rgb(initColor.red, initColor.green, initColor.blue)
                this.colorChangeListener = onColorChanged
            }
        },
        update = { view ->
            view.rgb = android.graphics.Color.rgb(initColor.red, initColor.green, initColor.blue)
        }
    )
}

@Composable
fun BrightnessSlider(
    color: Int = android.graphics.Color.WHITE,
    value: Int,
    onBrightnessChanged: (Int) -> Unit
) {
    //var _brightness by remember { mutableStateOf(value) }
    AndroidView(
        factory = { context ->
            GradientSeekBar(context).apply {
                setBlackToColor(color)
                offset = (value.toFloat() / 100)
                orientation = GradientSeekBar.Orientation.HORIZONTAL
                setColors(startColor, endColor)
                colorChangeListener = { brightness, _ ->
                    //_brightness = (brightness * 100).toInt()
                    onBrightnessChanged((brightness * 100).toInt())
                }
            }
        },
        update = {
            it.setBlackToColor(color)
            it.setColors(it.startColor,it.endColor)
            it.offset = (value.toFloat() / 100)
        })
}