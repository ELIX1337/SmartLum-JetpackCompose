package com.example.smartlumnew.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun Feed() {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Feed screen",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize())
    }
}