package com.example.smartlumnew.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.models.data.PeripheralDataElement

@Composable
fun ValuePicker(
    items: List<PeripheralDataElement>,
    selected: PeripheralDataElement,
    onSelected: (PeripheralDataElement) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(0.dp, 5.dp)
    ) {
        items(items = items, itemContent = { item ->
            ValuePickerItem(
                title = stringResource(item.elementNameStringID),
                isSelected = item == selected) {
                onSelected(item)
            }
        })
    }
}

@Composable
fun ValuePickerItem(
    title: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(44.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(title)
        }
    }
}