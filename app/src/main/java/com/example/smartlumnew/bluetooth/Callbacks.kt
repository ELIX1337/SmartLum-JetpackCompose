package com.example.smartlumnew.bluetooth.callbacks

import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.util.Log
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback
import no.nordicsemi.android.ble.data.Data

abstract class FirmwareVersionDataCallback : ProfileDataCallback, FirmwareVersionCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        Log.e("TAG", "onDataReceived: FIRMWARE VERSION - ${data.getIntValue(Data.FORMAT_UINT8, 0)}" )
        if (data.size() != 2) {
            onInvalidDataReceived(device, data)
            return
        }
        val value = data.getIntValue(Data.FORMAT_UINT8, 0)!!
        onFirmwareVersionReceived(device, value)
    }
}

interface FirmwareVersionCallback {
    fun onFirmwareVersionReceived(device: BluetoothDevice, version: Int)
}

interface AnimationDirectionCallback {
    fun onAnimationDirectionReceived(device: BluetoothDevice, direction: Int)
}

abstract class AnimationDirectionDataCallback : ProfileDataCallback, AnimationDirectionCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data)
            return
        }
        val value = data.getIntValue(Data.FORMAT_UINT8, 0)!!
        onAnimationDirectionReceived(device, value)
    }
}

interface AnimationModeCallback {
    fun onAnimationModeReceived(device: BluetoothDevice, mode: Int)
}

abstract class AnimationModeDataCallback : ProfileDataCallback, AnimationModeCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data)
            return
        }
        val value = data.getIntValue(Data.FORMAT_UINT8, 0)!!
        onAnimationModeReceived(device, value)
    }
}

interface AnimationSpeedCallback {
    fun onAnimationSpeedReceived(device: BluetoothDevice, speed: Int)
}

abstract class AnimationSpeedDataCallback : ProfileDataCallback, AnimationSpeedCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data)
            return
        }
        val value = data.getIntValue(Data.FORMAT_UINT8, 0)!!
        onAnimationSpeedReceived(device, value)
    }
}

interface AnimationStepCallback {
    fun onAnimationStepReceived(device: BluetoothDevice, step: Int)
}

abstract class AnimationStepDataCallback : ProfileDataCallback, AnimationStepCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data)
            return
        }
        val value = data.getIntValue(Data.FORMAT_UINT8, 0)!!
        onAnimationStepReceived(device, value)
    }
}

interface ColorCallback {
    fun onColorReceived(device: BluetoothDevice, color: Int)
}

abstract class ColorDataCallback : ProfileDataCallback, ColorCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 3) {
            onInvalidDataReceived(device, data)
        } else {
            data.value
            val red = data.getIntValue(Data.FORMAT_UINT8, 0)!!
            val green = data.getIntValue(Data.FORMAT_UINT8, 1)!!
            val blue = data.getIntValue(Data.FORMAT_UINT8, 2)!!
            val color = Color.rgb(red, green, blue)
            onColorReceived(device, color)
            Log.e("TAG", "onDataReceived: ${color}" )
        }
    }
}

interface RandomColorCallback {
    fun onRandomColorState(device: BluetoothDevice, state: Boolean)
}

abstract class RandomColorDataCallback : ProfileDataCallback, RandomColorCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data)
            return
        }
        when (data.getIntValue(Data.FORMAT_UINT8, 0)!!) {
            STATE_ENABLED  -> onRandomColorState(device, true)
            STATE_DISABLED -> onRandomColorState(device, false)
            else           -> onInvalidDataReceived(device, data)
        }
    }

    companion object {
        private const val STATE_DISABLED = 0x00
        private const val STATE_ENABLED = 0x01
    }
}