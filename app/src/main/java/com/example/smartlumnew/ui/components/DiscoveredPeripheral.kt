package com.example.smartlumnew.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.R
import com.example.smartlumnew.bluetooth.DiscoveredBluetoothDevice

@Composable
fun DiscoveredPeripheralsList(
    peripherals: List<DiscoveredBluetoothDevice?>?,
    onPeripheralSelected: (DiscoveredBluetoothDevice) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp, 16.dp)
    ) {
        if (peripherals != null) {
            items(peripherals) { result ->
                if (result != null) {
                    DiscoveredPeripheral(
                        result,
                        onPeripheralSelected = onPeripheralSelected)
                }
            }
        }
    }
}

@Composable
fun DiscoveredPeripheral(
    scanResult: DiscoveredBluetoothDevice,
    onPeripheralSelected: (DiscoveredBluetoothDevice) -> Unit) {
    val peripheralName = scanResult.device.name ?: "Unknown name"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onPeripheralSelected(scanResult) },
        shape = MaterialTheme.shapes.large,
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.image_torchere),
                contentDescription = "Discovered peripheral image",
                contentScale = ContentScale.Crop,
                alignment = Alignment.BottomStart,
                modifier = Modifier.weight(2f)
            )
            Column(
                modifier = Modifier
                    .weight(4f)
                    .padding(16.dp, 16.dp)
                    .fillMaxHeight()
            ) {
                Text(
                    text = peripheralName,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = "Torchere",
                    style = MaterialTheme.typography.overline
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_chevron_right_24),
                contentDescription = "Discovered peripheral action icon",
                modifier = Modifier
                    .weight(1f))
        }

    }
}