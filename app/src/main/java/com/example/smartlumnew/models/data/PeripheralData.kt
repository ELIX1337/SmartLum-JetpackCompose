package com.example.smartlumnew.models.data

import android.graphics.Color
import android.util.Log
import androidx.annotation.StringRes
import com.example.smartlumnew.R
import no.nordicsemi.android.ble.data.Data

object PeripheralData {
    private const val STATE_OFF: Byte = 0x00
    private const val STATE_ON:  Byte = 0x01

    fun setTrue(): Data {
        return Data.opCode(STATE_ON)
    }

    fun setFalse(): Data {
        return Data.opCode(STATE_OFF)
    }
}

object PeripheralDataModel {
    var primaryColor:       Color? = null
    var secondaryColor:     Color? = null
    var randomColor:        Boolean? = null
    var animationMode:      FlClassicAnimations? = null
    var animationDirection: PeripheralAnimationDirections? = null
    var animationOnSpeed:   Int? = null
    var animationOffSpeed:  Int? = null
    var animationStep:      Int? = null
}

enum class AnimationSettings {
    PrimaryColor,
    SecondaryColor,
    RandomColor,
    Direction,
    Speed,
    Step
}

interface PeripheralDataElement {
    val code: Int
    @get:StringRes
    val elementName: Int
}

enum class SlStandartAnimations(val supportingSettings: List<AnimationSettings>): PeripheralDataElement {
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
        override val elementName: Int
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
        override val elementName: Int
            get() = R.string.peripheral_animation_mode_name_wave
    };

    companion object {
        fun valueOf(code: Int): SlStandartAnimations? = SlStandartAnimations.values().find { it.code == code }
    }
}

enum class FlClassicAnimations(val supportingSettings: List<AnimationSettings>): PeripheralDataElement {

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
        override val elementName: Int
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
        override val elementName: Int
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
        override val elementName: Int
            get() = R.string.peripheral_animation_mode_name_transfusion
    },

    RainbowTransfusion(
        listOf(
            AnimationSettings.Speed
        )
    ) {
        override val code: Int
            get() = 4
        override val elementName: Int
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
        override val elementName: Int
            get() = R.string.peripheral_animation_mode_name_rainbow
    },

    Static(
        listOf(
            AnimationSettings.PrimaryColor
        )
    ) {
        override val code: Int
            get() = 6
        override val elementName: Int
            get() = R.string.peripheral_animation_mode_name_static
    };

    companion object {
        fun valueOf(code: Int): FlClassicAnimations? = values().find { it.code == code }
    }
}

enum class PeripheralAnimationDirections: PeripheralDataElement {

    FromBottom {
        override val code: Int
            get() = 1
        override val elementName: Int
            get() = R.string.peripheral_animation_direction_from_bottom
    },

    FromTop {
        override val code: Int
            get() = 2
        override val elementName: Int
            get() = R.string.peripheral_animation_direction_from_top
    },

    ToCenter {
        override val code: Int
            get() = 3
        override val elementName: Int
            get() = R.string.peripheral_animation_direction_to_center
    },

    FromCenter {
        override val code: Int
            get() = 4
        override val elementName: Int
            get() = R.string.peripheral_animation_direction_from_center
    };

    companion object {
        fun valueOf(code: Int): PeripheralAnimationDirections? = values().find { it.code == code }
    }

}

fun parseDoubleByteData(data: Data, formatType: Int, reversed: Boolean): Int {
    var b: Int = data.getIntValue(formatType, if (!reversed) 0 else 1)!!
    b = b shl 8
    b = b or data.getIntValue(formatType, if (!reversed) 1 else 0)!!
    Log.e("TAG", "onDataReceived: \n int = $b" )
    return b
}

fun createDoubleByteData(data: Int): ByteArray {
    val array = ByteArray(2 )
    array[0] = (data shr 8).toByte()
    array[1] = (data and 0xFF).toByte()
    return array
}



