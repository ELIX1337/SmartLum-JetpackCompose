package com.example.smartlumnew.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.antonpopoff.colorwheel.ColorWheel
import com.github.antonpopoff.colorwheel.gradientseekbar.GradientSeekBar
import com.github.antonpopoff.colorwheel.gradientseekbar.setBlackToColor

fun colorToHSV(color: Int): FloatArray {
    val array = FloatArray(3)
    Color.colorToHSV(color, array)
    return array
}

@Composable
fun ColorPicker(
    initColor: FloatArray,
    brightness: Boolean = true,
    onColorChanged: (Int) -> Unit,
) {
    var color by remember { mutableStateOf(initColor) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
    ) {
        AndroidView(
            factory = { context ->
                ColorWheel(context).apply {
                    this.rgb = Color.HSVToColor(color)
                    this.colorChangeListener = {
                        if (it != Color.HSVToColor(color)) {
                            color = colorToHSV(it)
                            onColorChanged(it)
                        }
                    }
                }
            },
            update = { view ->
                view.rgb = Color.HSVToColor(color)
            }
        )
        if (brightness) {
            BrightnessSlider(
                color = Color.HSVToColor(color),
                value = color[2],
                onBrightnessChanged = {
                    if (it != color[2]) {
                        color[2] = it
                        onColorChanged(Color.HSVToColor(color))
                    }
                }
            )
        }
    }
}

@Composable
fun BrightnessSlider(
    color: Int = Color.WHITE,
    value: Float,
    onBrightnessChanged: (Float) -> Unit
) {
    AndroidView(
        factory = { context ->
            GradientSeekBar(context).apply {
                setBlackToColor(color)
                offset = value
                orientation = GradientSeekBar.Orientation.HORIZONTAL
                setColors(startColor, endColor)
                colorChangeListener = { brightness, _ ->
                    if (value != brightness) {
                        onBrightnessChanged(brightness)
                    }
                }
            }
        },
        update = {
            it.setBlackToColor(color)
            it.setColors(it.startColor,it.endColor)
            it.offset = value
        })
}