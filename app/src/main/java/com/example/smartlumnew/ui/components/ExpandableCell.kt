package com.example.smartlumnew.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.ui.theme.SlDarkBlue
import com.example.smartlumnew.ui.theme.isAppInDarkTheme

@Composable
fun ExpandableCell(
    modifier: Modifier = Modifier,
    headerContent: @Composable (() -> Unit),
    bodyContent: @Composable (() -> Unit)? = null,
) {
    val headerBackground = if (isAppInDarkTheme()) Color.White else SlDarkBlue
    val headerText = if (isAppInDarkTheme()) SlDarkBlue else Color.White
    val styledHeaderContent: @Composable (() -> Unit)? = bodyContent?.let {
        @Composable {
            val style = MaterialTheme.typography.body2.copy(
                color = headerText
            )
            ProvideTextStyle(style, content = headerContent)
        }
    }
    val styledBodyContent: @Composable (() -> Unit)? = bodyContent?.let {
        @Composable {
            val style = MaterialTheme.typography.body2.copy(
                textAlign = TextAlign.Center,
            )
            ProvideTextStyle(style, content = bodyContent)
        }
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = modifier
            .animateContentSize(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
            .fillMaxWidth()
            .defaultMinSize(minHeight = 44.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, headerBackground.copy(alpha = 0.2f)),
        color = headerBackground,
        elevation = 1.dp
    ) {
        Column {
            Surface(
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .fillMaxWidth()
                    .padding(16.dp, 12.dp),
                color = headerBackground,
                ) {
                Row {
                    Box(Modifier.weight(9f)) {
                        styledHeaderContent?.invoke()
                    }
                    Icon(
                        imageVector = if (!expanded) Icons.Rounded.ExpandMore else Icons.Rounded.ExpandLess,
                        contentDescription = "Expand icon",
                        tint = MaterialTheme.colors.surface
                    )
                }
            }
            if (expanded) {
                styledBodyContent?.let {
                    Surface {
                        Divider()
                        Box(
                            Modifier
                                .padding(16.dp, 12.dp)) {
                            it()
                        }
                    }
                }
            }
        }
    }
}