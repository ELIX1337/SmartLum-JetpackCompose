package com.example.smartlumnew.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.R
import com.example.smartlumnew.models.bluetooth.DiscoveredPeripheral
import com.example.smartlumnew.models.bluetooth.PeripheralProfileEnum
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues

@Composable
fun PeripheralsList(
    modifier: Modifier = Modifier,
    peripherals: List<DiscoveredPeripheral?>?,
    onPeripheralSelected: (DiscoveredPeripheral) -> Unit,
    contentPadding: PaddingValues
) {
    if (peripherals != null) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.systemBars,
                applyTop = true,
                applyBottom = true,
                additionalTop = dimensionResource(id = R.dimen.AppBar_height) + 12.dp,
                additionalBottom = 6.dp
            )
        ) {
            items(peripherals, key = { result -> result.hashCode() }) { peripheral ->
                if (peripheral != null) {
                    PeripheralCard(
                        peripheralProfile = peripheral.type,
                        onPeripheralSelected = { onPeripheralSelected(peripheral) }
                    )

                }
            }
        }
    } else {
        Column(
            Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NoDevices()
        }
    }
}

@Composable
fun NoDevices() {
    Card(
        backgroundColor = colorResource(id = R.color.black_alpha),
        elevation = 0.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(6.dp)
        ) {
            Icon(
                Icons.Rounded.Search,
                contentDescription = "Search"
            )
            Text(
                text = stringResource(R.string.peripheral_list_text_no_devices_found),
                textAlign = TextAlign.Center
            )
        }

    }
}

@Composable
fun PeripheralCard(
    peripheralProfile: PeripheralProfileEnum,
    onPeripheralSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(Dp.Unspecified, 100.dp, Dp.Unspecified, 150.dp)
            .clickable { onPeripheralSelected() },
        shape = MaterialTheme.shapes.medium,
        elevation = 8.dp
    ) {
        Image(
            painter = painterResource(peripheralProfile.image),
            contentDescription = "Discovered peripheral image",
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter,
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp, 16.dp)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(9f)
                    .padding(0.dp, 0.dp, 16.dp, 0.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    TextCard(
                        text = stringResource(peripheralProfile.peripheralName),
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(1.dp)
                    )
                    TextCard(
                        text = stringResource(peripheralProfile.type),
                        style = MaterialTheme.typography.overline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(1.dp)
                    )
                }
                TextCard(
                    text = stringResource(peripheralProfile.description),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(1.dp)
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_chevron_right_24),
                    contentDescription = "Discovered peripheral action icon",
                    tint = Color.White,
                    modifier = Modifier
                        .background(Color(0, 0, 0, 0x33), CircleShape)
                )
            }
        }
    }
}