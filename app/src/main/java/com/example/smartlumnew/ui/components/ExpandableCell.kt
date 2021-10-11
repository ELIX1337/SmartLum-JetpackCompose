package com.example.smartlumnew.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.ui.theme.contrastTransparent

@Composable
fun ExpandableCell(
    modifier: Modifier = Modifier,
    headerContent: @Composable (() -> Unit),
    bodyContent: @Composable (() -> Unit)? = null,
) {
    val styledContent: @Composable (() -> Unit)? = bodyContent?.let {
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
            .border(BorderStroke(1.dp, contrastTransparent()))
            .fillMaxWidth()
            .defaultMinSize(minHeight = 44.dp),
        elevation = 1.dp
    ) {
        Column {
            Surface(
                Modifier
                    .clickable { expanded = !expanded }
                    .fillMaxWidth()
                    .padding(16.dp, 12.dp)) {
                Row {
                    Box(Modifier.weight(9f)) {
                        headerContent()
                    }
                    Icon(
                        imageVector = if (!expanded) Icons.Rounded.ExpandMore else Icons.Rounded.ExpandLess,
                        contentDescription = "Expand icon")
                }
            }
            if (expanded) {
                styledContent?.let {
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