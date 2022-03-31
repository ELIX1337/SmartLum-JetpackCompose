package com.example.smartlumnew.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.lang.NumberFormatException

@Preview(
    showSystemUi = true,
    showBackground = true)
@Composable
fun StepperCellPreview() {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        StepperCell(
            title = "Some title",
            value = 5,
            onStepChanged = { }
        )
    }
}

@Composable
fun StepperCell(
    title: String,
    value: Int,
    minValue: Int = 0,
    maxValue: Int = 10,
    additionalContent: @Composable (() -> Unit)? = null,
    onStepChanged: (Int) -> Unit
) {
    var _value by remember { mutableStateOf(value) }
    val focusManager = LocalFocusManager.current

    Cell(
        mainContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = title,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Row(
                    modifier = Modifier.padding(4.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(12.dp, 0.dp)
                            //.width(36.dp),
                            .weight(1f),
                        value = _value.toString(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onStepChanged(_value)
                                focusManager.clearFocus()
                            }
                        ),
                        singleLine = true,
                        onValueChange = {
                            if (it.isNotEmpty()) {
                                try {
                                    it.toInt().let { number ->
                                        when {
                                            number > maxValue -> {
                                                _value = maxValue
                                            }
                                            number < minValue -> {
                                                _value = minValue
                                            }
                                            else -> {
                                                _value = number
                                            }
                                        }
                                    }
                                } catch (e: NumberFormatException) {
                                }
                            } else {
                                _value = minValue
                            }
                        })
                    Stepper(
                        modifier = Modifier.weight(3f),
                        value = _value,
                        minValue = minValue,
                        maxValue = maxValue
                    ) {
                        _value = it
                        onStepChanged(it)
                    }
                }
            }
        },
        additionalContent = additionalContent
    )
}

@Composable
fun Stepper(
    modifier: Modifier = Modifier,
    value: Int = 0,
    minValue: Int = 0,
    maxValue: Int = 10,
    onStepChanged: (Int) -> Unit
) {
    Row(modifier = modifier) {
        Button(
            contentPadding = PaddingValues(0.dp),
            onClick = { if (value >= minValue) onStepChanged(value - 1) },
            enabled = value > minValue,
            shape = RoundedCornerShape(50, 0, 0, 50)
        ) {
            Text(
                text = "-",
                style = MaterialTheme.typography.h6,
            )
        }
        Button(
            contentPadding = PaddingValues(0.dp),
            onClick = { if (value <= maxValue) onStepChanged(value + 1) },
            enabled = value < maxValue,
            shape = RoundedCornerShape(0, 50, 50, 0)
        ) {
            Text(
                text = "+",
                style = MaterialTheme.typography.h6,
            )
        }
    }
}