package com.example.smartlumnew.models.bluetooth

import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.util.Log
import androidx.compose.ui.res.stringArrayResource
import com.example.smartlumnew.models.data.parseDoubleByteData
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback
import no.nordicsemi.android.ble.data.Data

/**
 * Здесь описаны различные колбэки Bluetooth.
 * В зависимости от того, какие данные будут считаны, присваиваешь соответсвующий колбэк.
 * Конкретная реализация происходит в самом PeripheralManager'e через абстрактный класс.
 */

interface IntegerCallback {
    fun onIntegerValueReceived(device: BluetoothDevice, data: Int)
}

interface BooleanCallback {
    fun onBooleanReceived(device: BluetoothDevice, state: Boolean)
}

interface StringCallback {
    fun onStringReceived(device: BluetoothDevice, string: String)
}

interface RGBCallback {
    fun onRGBReceived(device: BluetoothDevice, color: Int)
}

abstract class StringDataCallback : ProfileDataCallback, StringCallback {

    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        var string = ""
        for (element in 0 until data.size()) {
            string += data.getByte(element)
        }
        data.value?.let {
            onStringReceived(device, string)
            return
        }
        onInvalidDataReceived(device, data)
    }
}

abstract class SingleByteDataCallback : ProfileDataCallback, IntegerCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data)
            return
        }
        val value = data.getIntValue(Data.FORMAT_UINT8, 0)!!
        onIntegerValueReceived(device, value)
    }
}

abstract class DoubleByteDataCallback : ProfileDataCallback, IntegerCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 2) {
            onInvalidDataReceived(device, data)
            return
        }
        val value = parseDoubleByteData(data, Data.FORMAT_UINT8, false)
        onIntegerValueReceived(device, value)
    }
}

abstract class BooleanDataCallback : ProfileDataCallback, BooleanCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data)
            return
        }
        when (data.getIntValue(Data.FORMAT_UINT8, 0)!!) {
            STATE_TRUE  -> onBooleanReceived(device, true)
            STATE_FALSE -> onBooleanReceived(device, false)
            else        -> onInvalidDataReceived(device, data)
        }
    }

    companion object {
        private const val STATE_FALSE = 0x00
        private const val STATE_TRUE  = 0x01
    }
}

abstract class RGBDataCallback : ProfileDataCallback, RGBCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 3) {
            onInvalidDataReceived(device, data)
        } else {
            data.value
            val red = data.getIntValue(Data.FORMAT_UINT8, 0)!!
            val green = data.getIntValue(Data.FORMAT_UINT8, 1)!!
            val blue = data.getIntValue(Data.FORMAT_UINT8, 2)!!
            val color = Color.rgb(red, green, blue)
            onRGBReceived(device, color)
        }
    }
}