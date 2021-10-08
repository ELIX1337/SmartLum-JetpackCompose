package com.example.smartlumnew.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SwitchCell(
    title: String,
    initValue: Boolean,
    additionalContent: @Composable (() -> Unit)? = null,
    onStateChange: (Boolean) -> Unit,
) {
    Cell(
        mainContent = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = title)
                }
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Switch(checked = initValue, onCheckedChange = onStateChange)
                }
            }
        },
        additionalContent = additionalContent
    )
}