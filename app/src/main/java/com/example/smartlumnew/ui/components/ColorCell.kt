package com.example.smartlumnew.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColorCell(
    title: String,
    color: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(44.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(25),
        onClick = onClick
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .padding(14.dp, 10.dp)
                    .weight(5f),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text = title)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .width(10.dp)
                    .fillMaxHeight()
                    .background(Color(color))
            )
        }

    }
}