package com.example.smartlumnew.models.data.peripheralData

import com.example.smartlumnew.R
import com.example.smartlumnew.models.data.AnimationSettings
import com.example.smartlumnew.models.data.PeripheralDataElement

enum class FlClassicAnimations(val supportingSettings: List<AnimationSettings>):
    PeripheralDataElement {

    Tetris(
        listOf(
            AnimationSettings.PrimaryColor,
            AnimationSettings.SecondaryColor,
            AnimationSettings.RandomColor,
            AnimationSettings.Direction,
            AnimationSettings.Speed)
    ) {
        override val code: Int
            get() = 1
        override val elementNameStringID: Int
            get() = R.string.peripheral_animation_mode_name_tetris
    },

    Wave(
        listOf(
            AnimationSettings.PrimaryColor,
            AnimationSettings.SecondaryColor,
            AnimationSettings.Direction,
            AnimationSettings.Speed,
            AnimationSettings.Step)
    ) {
        override val code: Int
            get() = 2
        override val elementNameStringID: Int
            get() = R.string.peripheral_animation_mode_name_wave
    },

    Transfusion(
        listOf(
            AnimationSettings.PrimaryColor,
            AnimationSettings.SecondaryColor,
            AnimationSettings.RandomColor,
            AnimationSettings.Speed)
    ) {
        override val code: Int
            get() = 3
        override val elementNameStringID: Int
            get() = R.string.peripheral_animation_mode_name_transfusion
    },

    RainbowTransfusion(
        listOf(
            AnimationSettings.Speed
        )
    ) {
        override val code: Int
            get() = 4
        override val elementNameStringID: Int
            get() = R.string.peripheral_animation_mode_rainbow_transfusion
    },

    Rainbow(
        listOf(
            AnimationSettings.Direction,
            AnimationSettings.Speed
        )
    ) {
        override val code: Int
            get() = 5
        override val elementNameStringID: Int
            get() = R.string.peripheral_animation_mode_name_rainbow
    },

    Static(
        listOf(
            AnimationSettings.PrimaryColor
        )
    ) {
        override val code: Int
            get() = 6
        override val elementNameStringID: Int
            get() = R.string.peripheral_animation_mode_name_static
    };

    companion object {
        fun valueOf(code: Int): FlClassicAnimations? = values().find { it.code == code }
    }
}

enum class FlClassicAnimationDirections: PeripheralDataElement {

    FromBottom {
        override val code: Int
            get() = 1
        override val elementNameStringID: Int
            get() = R.string.peripheral_animation_direction_from_bottom
    },

    FromTop {
        override val code: Int
            get() = 2
        override val elementNameStringID: Int
            get() = R.string.peripheral_animation_direction_from_top
    },

    ToCenter {
        override val code: Int
            get() = 3
        override val elementNameStringID: Int
            get() = R.string.peripheral_animation_direction_to_center
    },

    FromCenter {
        override val code: Int
            get() = 4
        override val elementNameStringID: Int
            get() = R.string.peripheral_animation_direction_from_center
    };

    companion object {
        fun valueOf(code: Int): FlClassicAnimationDirections? = values().find { it.code == code }
    }

}