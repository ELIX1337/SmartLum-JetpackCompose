package com.example.smartlumnew.models.data

import android.graphics.Color
import android.util.Log
import androidx.annotation.StringRes
import com.example.smartlumnew.R
import no.nordicsemi.android.ble.data.Data

/**
 * Настройки устройства требуют кодировки (анимация, режимы работы и тп.)
 * Код (значение) оговаривается заранее.
 */
interface PeripheralDataElement {
    val code: Int
    @get:StringRes
    // Название. Вместо String используется ID на stringResource (для более простой локализации)
    val elementNameStringID: Int
}

/**
 * Просто упростил отправку boolean по Bluetooth
 */
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

/**
 * Список настроек, которые может поддеживать анимация
 * Некоторые анимации, например, Радуга, уже подразумевают что цвета идут по радуге,
 * соответсвенно нужно убрать поля управления цветом когда выбрана данная анимация.
 */
enum class AnimationSettings {
    PrimaryColor,
    SecondaryColor,
    RandomColor,
    Direction,
    Speed,
    Step
}

/**
 * Метод, который собирает из 2 байт одно число.
 * reversed - начинает собирать с конца или с начала.
 * Пояснение:
 * Когда нужно отправить число большее чем 255 по Bluetooth, то нужно его разбить на 2 (или более) байтов.
 * Этот метод спарсит такой пакет на приеме.
 */
fun parseDoubleByteData(data: Data, formatType: Int, reversed: Boolean): Int {
    var b: Int = data.getIntValue(formatType, if (!reversed) 0 else 1)!!
    b = b shl 8
    b = b or data.getIntValue(formatType, if (!reversed) 1 else 0)!!
    Log.e("TAG", "onDataReceived: \n int = $b" )
    return b
}

/**
 * Метод, который собирает 2 байта данных для отправки по BLE (когда одного недостаточно).
 */
fun createDoubleByteData(data: Int): ByteArray {
    val array = ByteArray(2 )
    array[0] = (data shr 8).toByte()
    array[1] = (data and 0xFF).toByte()
    return array
}



