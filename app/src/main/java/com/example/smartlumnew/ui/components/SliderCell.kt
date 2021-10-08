package com.example.smartlumnew.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview(
    showSystemUi = false
)
internal fun SliderCellPreview() {
    SliderCell(
        title = "Some long text title with more and more text that cannot fit into",
        initValue = 2f,
        valueRange = 0f..10f
    ) { }
}

@Composable
fun SliderCell(
    title: String,
    initValue: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    leftIcon: @Composable () -> Unit = { },
    rightIcon: @Composable () -> Unit = { },
    additionalContent: @Composable (() -> Unit)? = null,
    onValueChanged: (Float) -> Unit
) {
    Cell(
        mainContent = {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(title)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    leftIcon()
                    Slider(
                        modifier = Modifier.weight(1f),
                        value = initValue,
                        valueRange = valueRange,
                        onValueChange = { onValueChanged(it) }
                    )
                    rightIcon()
                }
            }
        },
        additionalContent = additionalContent
    )
}