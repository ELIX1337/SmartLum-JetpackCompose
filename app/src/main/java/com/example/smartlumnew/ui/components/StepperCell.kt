package com.example.smartlumnew.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StepperCell(
    title: String,
    initValue: Int,
    minValue: Int = 0,
    maxValue: Int = 10,
    additionalContent: @Composable (() -> Unit)? = null,
    onStepChanged: (Int) -> Unit
) {
    var value by remember { mutableStateOf(initValue) }

    Cell(
        mainContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = title,
                    modifier = Modifier
                        .weight(1f))
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.weight(1f))
                Stepper(
                    initValue = initValue,
                    minValue = minValue,
                    maxValue = maxValue
                ) {
                    value = it
                    onStepChanged(it)
                }
            }
        },
        additionalContent = additionalContent
    )
}

@Composable
fun Stepper(
    modifier: Modifier = Modifier,
    initValue: Int = 0,
    minValue: Int = 0,
    maxValue: Int = 10,
    onStepChanged: (Int) -> Unit
) {
    var currentValue by remember { mutableStateOf(initValue) }
    Row(modifier = modifier) {
        Button(
            contentPadding = PaddingValues(0.dp),
            onClick = { if (currentValue >= minValue) onStepChanged(--currentValue) },
            enabled = currentValue > minValue,
            shape = RoundedCornerShape(50, 0, 0, 50)
        ) {
            Text(
                text = "-",
                style = MaterialTheme.typography.h6,
            )
        }
        Button(
            contentPadding = PaddingValues(0.dp),
            onClick = { if (currentValue <= maxValue) onStepChanged(++currentValue) },
            enabled = currentValue < maxValue,
            shape = RoundedCornerShape(0, 50, 50, 0)
        ) {
            Text(
                text = "+",
                style = MaterialTheme.typography.h6,
            )
        }
    }
}