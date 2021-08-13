package com.example.smartlumnew.ui.peripheral

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun PeripheralScreen(onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        Button(onClick) {
            Text("Peripheral screen")
        }
    }
}