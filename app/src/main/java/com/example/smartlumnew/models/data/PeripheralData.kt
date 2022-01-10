package com.example.smartlumnew.models.data

import android.graphics.Color
import android.util.Log
import androidx.annotation.StringRes
import com.example.smartlumnew.R
import no.nordicsemi.android.ble.data.Data

interface PeripheralDataElement {
    val code: Int
    @get:StringRes
    val elementNameStringID: Int
}

object PeripheralDataModel {
    var primaryColor:       Color? = null
    var secondaryColor:     Color? = null
    var randomColor:        Boolean? = null
    var animationMode:      PeripheralDataElement? = null
    var animationDirection: PeripheralDataElement? = null
    var animationOnSpeed:   Int? = null
    var animationOffSpeed:  Int? = null
    var animationStep:      Int? = null
}

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

enum class AnimationSettings {
    PrimaryColor,
    SecondaryColor,
    RandomColor,
    Direction,
    Speed,
    Step
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



