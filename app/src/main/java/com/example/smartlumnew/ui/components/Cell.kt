package com.example.smartlumnew.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Cell(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = MaterialTheme.colors.surface,
    elevation: Dp = 1.dp,
    mainContent: @Composable (() -> Unit),
    additionalContent: @Composable (() -> Unit)? = null,
) {
    val styledContent: @Composable (() -> Unit)? = additionalContent?.let {
        @Composable {
            val style = MaterialTheme.typography.body2.copy()
            ProvideTextStyle(style, content = additionalContent)
        }
    }
    Card(
        modifier = modifier
            .defaultMinSize(minHeight = 44.dp),
        shape = shape,
        backgroundColor = backgroundColor,
        elevation = elevation
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 12.dp)
        ) {
            Box {
                mainContent()
            }
            styledContent?.let {
                Divider(modifier = Modifier.padding(0.dp, 8.dp))
                it()
            }
        }
    }
}