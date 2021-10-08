package com.example.smartlumnew.models.data

import android.graphics.Color
import android.util.Log
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
    var animationMode:      PeripheralAnimations? = null
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
    val name: String
}

enum class PeripheralAnimations(val supportingSettings: List<AnimationSettings>): PeripheralDataElement {

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
    },

    RainbowTransfusion(
        listOf(
            AnimationSettings.Speed
        )
    ) {
        override val code: Int
            get() = 4
    },

    Rainbow(
        listOf(
            AnimationSettings.Direction,
            AnimationSettings.Speed
        )
    ) {
        override val code: Int
            get() = 5
    },

    Static(
        listOf(
            AnimationSettings.PrimaryColor
        )
    ) {
        override val code: Int
            get() = 6
    };

    companion object {
        fun valueOf(code: Int): PeripheralAnimations? = values().find { it.code == code }
    }
}

enum class PeripheralAnimationDirections: PeripheralDataElement {

    FromBottom {
        override val code: Int
            get() = 1
    },

    FromTop {
        override val code: Int
            get() = 2
    },

    ToCenter {
        override val code: Int
            get() = 3
    },

    FromCenter {
        override val code: Int
            get() = 4
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



