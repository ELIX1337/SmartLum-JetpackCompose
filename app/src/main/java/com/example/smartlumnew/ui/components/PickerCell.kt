package com.example.smartlumnew.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ValuePickerCell(
    title: String = "Null",
    value: String = "Null",
    showIcon: Boolean = true,
    additionalContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Cell(
        onClick = onClick,
        mainContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp, 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = title)
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(text = value)
                }
                if (showIcon) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Rounded.ArrowForwardIos,
                        contentDescription = "Open picker",
                        tint = MaterialTheme.colors.onSurface
                    )
                }
            }
        },
        additionalContent = additionalContent
    )

}