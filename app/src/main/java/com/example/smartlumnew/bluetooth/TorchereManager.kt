package com.example.smartlumnew.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.smartlumnew.bluetooth.callbacks.*
import java.util.*

class TorchereManager(context: Context) : BasePeripheralManager(context) {

    /** Torchere UUID  */
    companion object {
        val TORCHERE_SERVICE_UUID: UUID = UUID.fromString("BB930002-3CE1-4720-A753-28C0159DC777")
    }

    private var primaryColorCharacteristic       : BluetoothGattCharacteristic? = null
    private var secondaryColorCharacteristic     : BluetoothGattCharacteristic? = null
    private var randomColorCharacteristic        : BluetoothGattCharacteristic? = null
    private var animationModeCharacteristic      : BluetoothGattCharacteristic? = null
    private var animationOnSpeedCharacteristic   : BluetoothGattCharacteristic? = null
    private var animationOffSpeedCharacteristic  : BluetoothGattCharacteristic? = null
    private var animationDirectionCharacteristic : BluetoothGattCharacteristic? = null
    private var animationStepCharacteristic      : BluetoothGattCharacteristic? = null

    val primaryColor       = MutableLiveData<Int>()
    val secondaryColor     = MutableLiveData<Int>()
    val randomColor        = MutableLiveData<Boolean>()
    val animationMode      = MutableLiveData<Int>()
    val animationOnSpeed   = MutableLiveData<Int>()
    val animationOffSpeed  = MutableLiveData<Int>()
    val animationDirection = MutableLiveData<Int>()
    val animationStep      = MutableLiveData<Int>()

    private inner class TorcherePeripheralManagerGattCallback : BasePeripheralManagerGattCallback() {
        override fun initialize() {
            super.initialize()
            Log.e("TAG", "initialize: ")
            readCharacteristic(primaryColorCharacteristic).with(primaryColorCallback).enqueue()
            readCharacteristic(secondaryColorCharacteristic).with(secondaryColorCallback).enqueue()
            readCharacteristic(randomColorCharacteristic).with(randomColorCallback).enqueue()
            readCharacteristic(animationModeCharacteristic).with(animationModeCallback).enqueue()
            readCharacteristic(animationOnSpeedCharacteristic).with(animationOnSpeedCallback).enqueue()
            readCharacteristic(animationOffSpeedCharacteristic).with(animationOffSpeedCallback).enqueue()
            readCharacteristic(animationDirectionCharacteristic).with(animationDirectionCallback).enqueue()
            readCharacteristic(animationStepCharacteristic).with(animationStepCallback).enqueue()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            super.isRequiredServiceSupported(gatt)
            val colorService = gatt.getService(COLOR_SERVICE_UUID)
            val animationService = gatt.getService(ANIMATION_SERVICE_UUID)
            colorService?.let     { initColorCharacteristics(it) }
            animationService?.let { initAnimationCharacteristics(it) }
            for (service in gatt.services) {
                for (characteristic in service.characteristics) {
                    readCharacteristic(characteristic)
                    Log.e("TAG", "isRequiredServiceSupported - ${characteristic.uuid}")
                }
            }
            return colorService != null && animationService != null
        }

        override fun onDeviceDisconnected() {
            super.onDeviceDisconnected()
            primaryColorCharacteristic   = null
            secondaryColorCharacteristic = null
            randomColorCharacteristic    = null
        }
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return TorcherePeripheralManagerGattCallback()
    }

    private fun initColorCharacteristics(service: BluetoothGattService) {
        primaryColorCharacteristic   = service.getCharacteristic(COLOR_PRIMARY_CHARACTERISTIC_UUID)
        secondaryColorCharacteristic = service.getCharacteristic(COLOR_SECONDARY_CHARACTERISTIC_UUID)
        randomColorCharacteristic    = service.getCharacteristic(COLOR_RANDOM_CHARACTERISTIC_UUID)
    }

    private fun initAnimationCharacteristics(service: BluetoothGattService) {
        animationModeCharacteristic      = service.getCharacteristic(ANIMATION_MODE_CHARACTERISTIC_UUID)
        animationOnSpeedCharacteristic   = service.getCharacteristic(ANIMATION_ON_SPEED_CHARACTERISTIC_UUID)
        animationOffSpeedCharacteristic  = service.getCharacteristic(ANIMATION_OFF_SPEED_CHARACTERISTIC_UUID)
        animationDirectionCharacteristic = service.getCharacteristic(ANIMATION_DIRECTION_CHARACTERISTIC_UUID)
        animationStepCharacteristic      = service.getCharacteristic(ANIMATION_STEP_CHARACTERISTIC_UUID)
    }

    private val primaryColorCallback: ColorDataCallback = object : ColorDataCallback() {
        override fun onColorReceived(device: BluetoothDevice, color: Int) {
            Log.e("TAG", "onColorReceived: PRIMARY")
            primaryColor.postValue(color)
        }
    }
    private val secondaryColorCallback: ColorDataCallback = object : ColorDataCallback() {
        override fun onColorReceived(device: BluetoothDevice, color: Int) {
            Log.e("TAG", "onColorReceived: SECONDARY")
            secondaryColor.postValue(color)
        }
    }
    private val randomColorCallback: RandomColorDataCallback = object : RandomColorDataCallback() {
        override fun onRandomColorState(device: BluetoothDevice, state: Boolean) {
            Log.d("TAG", "onRandomColorState: $state")
            randomColor.postValue(state)
        }
    }
    private val animationModeCallback: AnimationModeDataCallback =
        object : AnimationModeDataCallback() {
            override fun onAnimationModeReceived(device: BluetoothDevice, mode: Int) {
                Log.d("TAG", "onAnimationModeReceived: $mode")
                animationMode.postValue(mode)
            }
        }
    private val animationOnSpeedCallback: AnimationSpeedDataCallback =
        object : AnimationSpeedDataCallback() {
            override fun onAnimationSpeedReceived(device: BluetoothDevice, speed: Int) {
                Log.d("TAG", "onAnimationOnStepReceived: $speed")
                animationOnSpeed.postValue(speed)
            }
        }
    private val animationOffSpeedCallback: AnimationSpeedDataCallback =
        object : AnimationSpeedDataCallback() {
            override fun onAnimationSpeedReceived(device: BluetoothDevice, speed: Int) {
                Log.d("TAG", "onAnimationOffStepReceived: $speed")
                animationOffSpeed.postValue(speed)
            }
        }
    private val animationDirectionCallback: AnimationDirectionDataCallback =
        object : AnimationDirectionDataCallback() {
            override fun onAnimationDirectionReceived(device: BluetoothDevice, direction: Int) {
                Log.d("TAG", "onAnimationDirectionReceived: $direction")
                animationDirection.postValue(direction)
            }
        }
    private val animationStepCallback: AnimationStepDataCallback =
        object : AnimationStepDataCallback() {
            override fun onAnimationStepReceived(device: BluetoothDevice, step: Int) {
                Log.d("TAG", "onAnimationStepReceived: $step")
                animationStep.postValue(step)
            }
        }

}

