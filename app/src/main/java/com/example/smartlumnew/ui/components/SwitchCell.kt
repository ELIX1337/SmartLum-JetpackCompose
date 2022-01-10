package com.example.smartlumnew.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SwitchCell(
    title: String,
    value: Boolean,
    additionalContent: @Composable (() -> Unit)? = null,
    onStateChange: (Boolean) -> Unit,
) {
    Cell(
        onClick = { onStateChange(!value) },
        mainContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = title)
                }
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Switch(checked = value, onCheckedChange = onStateChange)
                }
            }
        },
        additionalContent = additionalContent
    )
}