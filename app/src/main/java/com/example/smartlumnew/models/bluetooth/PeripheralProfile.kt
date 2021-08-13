package com.example.smartlumnew.models.bluetooth

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.example.smartlumnew.R

@Immutable
data class PeripheralProfile(
    val type: PeripheralType,
    val name: String,
    val description: String,
    @DrawableRes val image: Int
)

/**
 *  Static data
 */

val peripheralProfiles = listOf(
    PeripheralProfile(
        type = PeripheralType.FL_MINI,
        name = "Table lamp",
        description = "Such a small and minimalistic table lamp",
        image = R.drawable.image_torchere
    ),
    PeripheralProfile(
        type = PeripheralType.FL_CLASSIC,
        name = "Torchere",
        description = "Minimalistic floor lamp",
        image = R.drawable.image_torchere
    )
)