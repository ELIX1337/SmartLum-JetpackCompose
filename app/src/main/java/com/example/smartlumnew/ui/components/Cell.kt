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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Cell(
    mainContent: @Composable (() -> Unit),
    additionalContent: @Composable (() -> Unit)? = null,
) {
    val styledContent: @Composable (() -> Unit)? = additionalContent?.let {
        @Composable {
            val style = MaterialTheme.typography.body2.copy(
                textAlign = TextAlign.Center,
            )
            ProvideTextStyle(style, content = additionalContent)
        }
    }
    Card(
        modifier = Modifier
            .defaultMinSize(minHeight = 44.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = 1.dp
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