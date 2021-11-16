package com.example.smartlumnew.models.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.smartlumnew.models.data.FlClassicAnimations
import com.example.smartlumnew.models.data.PeripheralAnimationDirections
import com.example.smartlumnew.models.data.PeripheralData
import no.nordicsemi.android.ble.data.Data
import java.util.*

class TorchereManager(context: Context) : PeripheralManager(context) {

    /** Torchere UUID  */
    companion object {
        val FL_MINI_SERVICE_UUID : UUID = UUID.fromString("BB930002-3CE1-4720-A753-28C0159DC777")
        val TORCHERE_SERVICE_UUID: UUID = UUID.fromString("BB930001-3CE1-4720-A753-28C0159DC777")
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
    val animationMode      = MutableLiveData<FlClassicAnimations>()
    val animationOnSpeed   = MutableLiveData<Float>()
    val animationOffSpeed  = MutableLiveData<Int>()
    val animationDirection = MutableLiveData<PeripheralAnimationDirections>()
    val animationStep      = MutableLiveData<Int>()

    private inner class TorcherePeripheralManagerGattCallback : PeripheralManagerGattCallback() {
        override fun initialize() {
            super.initialize()
            Log.e("TAG", "initialize torchere manager: ")
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
            val colorService     = gatt.getService(LEGACY_COLOR_SERVICE_UUID)
            val animationService = gatt.getService(LEGACY_ANIMATION_SERVICE_UUID)
            colorService?.let     { initColorCharacteristics(it) }
            animationService?.let { initAnimationCharacteristics(it) }
            isInitialized.postValue(colorService != null && animationService != null)
            return colorService != null && animationService != null
        }

        override fun onServicesInvalidated() {
            super.onServicesInvalidated()
            close()
            primaryColorCharacteristic       = null
            secondaryColorCharacteristic     = null
            randomColorCharacteristic        = null
            animationModeCharacteristic      = null
            animationOnSpeedCharacteristic   = null
            animationOffSpeedCharacteristic  = null
            animationDirectionCharacteristic = null
            animationStepCharacteristic      = null
        }

    }

    override fun getGattCallback(): BleManagerGattCallback {
        super.getGattCallback()
        return TorcherePeripheralManagerGattCallback()
    }

    private fun initColorCharacteristics(service: BluetoothGattService) {
        primaryColorCharacteristic   = service.getCharacteristic(LEGACY_COLOR_PRIMARY_CHARACTERISTIC_UUID)
        secondaryColorCharacteristic = service.getCharacteristic(LEGACY_COLOR_SECONDARY_CHARACTERISTIC_UUID)
        randomColorCharacteristic    = service.getCharacteristic(LEGACY_COLOR_RANDOM_CHARACTERISTIC_UUID)
    }

    private fun initAnimationCharacteristics(service: BluetoothGattService) {
        animationModeCharacteristic      = service.getCharacteristic(LEGACY_ANIMATION_MODE_CHARACTERISTIC_UUID)
        animationOnSpeedCharacteristic   = service.getCharacteristic(LEGACY_ANIMATION_ON_SPEED_CHARACTERISTIC_UUID)
        animationOffSpeedCharacteristic  = service.getCharacteristic(LEGACY_ANIMATION_OFF_SPEED_CHARACTERISTIC_UUID)
        animationDirectionCharacteristic = service.getCharacteristic(LEGACY_ANIMATION_DIRECTION_CHARACTERISTIC_UUID)
        animationStepCharacteristic      = service.getCharacteristic(LEGACY_ANIMATION_STEP_CHARACTERISTIC_UUID)
    }

    private val primaryColorCallback: RGBDataCallback = object : RGBDataCallback() {
        override fun onRGBReceived(device: BluetoothDevice, color: Int) {
            Log.e("TAG", "onColorReceived: PRIMARY")
            primaryColor.postValue(color)
        }
    }
    private val secondaryColorCallback: RGBDataCallback = object : RGBDataCallback() {
        override fun onRGBReceived(device: BluetoothDevice, color: Int) {
            Log.e("TAG", "onColorReceived: SECONDARY")
            secondaryColor.postValue(color)
        }
    }
    private val randomColorCallback: BooleanDataCallback = object : BooleanDataCallback() {
        override fun onBooleanReceived(device: BluetoothDevice, state: Boolean) {
            Log.d("TAG", "onRandomColorState: $state")
            randomColor.postValue(state)
        }
    }
    private val animationModeCallback: SingleByteDataCallback =
        object : SingleByteDataCallback() {
            override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
                Log.d("TAG", "onAnimationModeReceived: $data")
                animationMode.postValue(FlClassicAnimations.valueOf(data))
            }
        }
    private val animationOnSpeedCallback: SingleByteDataCallback =
        object : SingleByteDataCallback() {
            override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
                Log.d("TAG", "onAnimationOnStepReceived: $data")
                animationOnSpeed.postValue(data.toFloat())
            }
        }
    private val animationOffSpeedCallback: SingleByteDataCallback =
        object : SingleByteDataCallback() {
            override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
                Log.d("TAG", "onAnimationOffStepReceived: $data")
                animationOffSpeed.postValue(data)
            }
        }
    private val animationDirectionCallback: SingleByteDataCallback =
        object : SingleByteDataCallback() {
            override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
                Log.d("TAG", "onAnimationDirectionReceived: $data")
                animationDirection.postValue(PeripheralAnimationDirections.valueOf(data))
            }
        }
    private val animationStepCallback: SingleByteDataCallback =
        object : SingleByteDataCallback() {
            override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
                Log.d("TAG", "onAnimationStepReceived: $data")
                animationStep.postValue(data)
            }
        }

    fun writePrimaryColor(color: Int) {
        if (primaryColorCharacteristic == null) return
        if (primaryColor.value == color) return
        val data = byteArrayOf(
            Color.red(color).toByte(), Color.green(color)
                .toByte(), Color.blue(color).toByte()
        )
        writeCharacteristic(
            primaryColorCharacteristic,
            data,
            WRITE_TYPE_NO_RESPONSE
        ).enqueue()
        primaryColor.postValue(color)
    }

    fun writeSecondaryColor(color: Int) {
        if (secondaryColorCharacteristic == null) return
        if (secondaryColor.value == color) return
        val data = byteArrayOf(
            Color.red(color).toByte(), Color.green(color)
                .toByte(), Color.blue(color).toByte()
        )
        writeCharacteristic(
            secondaryColorCharacteristic,
            data,
            WRITE_TYPE_NO_RESPONSE
        ).enqueue()
        secondaryColor.postValue(color)
    }

    fun writeRandomColor(state: Boolean) {
        if (randomColorCharacteristic == null) return
        writeCharacteristic(
            randomColorCharacteristic,
            if (state) PeripheralData.setTrue() else PeripheralData.setFalse(),
            WRITE_TYPE_NO_RESPONSE
        ).enqueue()
        randomColor.postValue(state)
    }

    fun writeAnimationMode(mode: Int) {
        if (animationModeCharacteristic == null) return
        writeCharacteristic(
            animationModeCharacteristic,
            Data.opCode(mode.toByte()),
            WRITE_TYPE_NO_RESPONSE
        ).enqueue()
        animationMode.postValue(FlClassicAnimations.valueOf(mode))
    }

    fun writeAnimationOnSpeed(speed: Float) {
        if (animationOnSpeedCharacteristic == null) return
        writeCharacteristic(
            animationOnSpeedCharacteristic,
            Data.opCode(speed.toInt().toByte()),
            WRITE_TYPE_NO_RESPONSE
        ).enqueue()
        Log.e("TAG", "writeAnimationOnSpeed: $speed" )
       // animationOnSpeed.postValue(speed)
    }

    fun writeAnimationOffSpeed(speed: Int) {
        if (animationOffSpeedCharacteristic == null) return
        writeCharacteristic(
            animationOffSpeedCharacteristic,
            Data.opCode(speed.toByte()),
            WRITE_TYPE_NO_RESPONSE
        ).enqueue()
        animationOffSpeed.postValue(speed)
    }

    fun writeAnimationDirection(direction: Int) {
        if (animationDirectionCharacteristic == null) return
        writeCharacteristic(
            animationDirectionCharacteristic,
            Data.opCode(direction.toByte()),
            WRITE_TYPE_NO_RESPONSE
        ).enqueue()
        animationDirection.postValue(PeripheralAnimationDirections.valueOf(direction))
    }

    fun writeAnimationStep(step: Int) {
        if (animationStepCharacteristic == null) return
        writeCharacteristic(
            animationStepCharacteristic,
            Data.opCode(step.toByte()),
            WRITE_TYPE_NO_RESPONSE
        ).enqueue()
        animationStep.postValue(step)
    }

}

