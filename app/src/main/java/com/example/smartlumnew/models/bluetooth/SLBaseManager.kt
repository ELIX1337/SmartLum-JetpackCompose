package com.example.smartlumnew.models.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.lifecycle.MutableLiveData
import com.example.smartlumnew.models.data.createDoubleByteData
import no.nordicsemi.android.ble.data.Data
import java.util.*

/**
 * Конкретная реализация менеджера.
 * Принцип абсолютно такой-же как и в родительком классе, просто дополняем его.
 */
class SLBaseManager(context: Context) : PeripheralManager(context) {

    /**
     * Рекламный UUID устройства SL-BASE. По идее не нужен, так как скинирование идет по маске
     */
    companion object {
        val SL_BASE_SERVICE_UUID: UUID = UUID.fromString("BB930003-3CE1-4720-A753-28C0159DC777")
    }

    private var ledStateCharacteristic:           BluetoothGattCharacteristic? = null
    private var ledBrightnessCharacteristic:      BluetoothGattCharacteristic? = null
    private var ledTimeoutCharacteristic:         BluetoothGattCharacteristic? = null
    private var animationOnSpeedCharacteristic:   BluetoothGattCharacteristic? = null
    private var topTriggerDistanceCharacteristic: BluetoothGattCharacteristic? = null
    private var botTriggerDistanceCharacteristic: BluetoothGattCharacteristic? = null

    val ledState                 = MutableLiveData<Boolean>()
    val ledBrightness            = MutableLiveData<Float>()
    val ledTimeout               = MutableLiveData<Int>()
    val animationOnSpeed         = MutableLiveData<Float>()
    val topSensorTriggerDistance = MutableLiveData<Float>()
    val botSensorTriggerDistance = MutableLiveData<Float>()

    /**
     * Переопределяем родительский класс, добавляем в него дополнительную реализацию
     * Не забываем вызвать super метод
     */
    private inner class SLBasePeripheralManagerGattCallback : PeripheralManagerGattCallback() {
        override fun initialize() {
            super.initialize()

            Log.e("TAG", "initialize SLBase manager: ")
            readCharacteristic(ledStateCharacteristic).with(ledStateCallback).enqueue()
            readCharacteristic(ledBrightnessCharacteristic).with(ledBrightnessCallback).enqueue()
            readCharacteristic(ledTimeoutCharacteristic).with(ledTimeoutCallback).enqueue()
            readCharacteristic(animationOnSpeedCharacteristic).with(animationOnSpeedCallback).enqueue()
            readCharacteristic(topTriggerDistanceCharacteristic).with(topSensorTriggerDistanceCallback).enqueue()
            readCharacteristic(botTriggerDistanceCharacteristic).with(botSensorTriggerDistanceCallback).enqueue()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            super.isRequiredServiceSupported(gatt)
            val ledService       = gatt.getService(LEGACY_LED_SERVICE_UUID)
            val animationService = gatt.getService(LEGACY_ANIMATION_SERVICE_UUID)
            val sensorService    = gatt.getService(LEGACY_SENSOR_SERVICE_UUID)
            ledService?.let       { initLedCharacteristics(it) }
            animationService?.let { initAnimationCharacteristics(it) }
            sensorService?.let    { initSensorCharacteristics(it) }
            Log.e("TAG", "isRequiredServiceSupported: ${ledService != null }" )
            return ledService != null && animationService != null && sensorService != null
        }

        override fun onServicesInvalidated() {
            super.onServicesInvalidated()
            ledStateCharacteristic           = null
            ledBrightnessCharacteristic      = null
            ledTimeoutCharacteristic         = null
            animationOnSpeedCharacteristic   = null
            topTriggerDistanceCharacteristic = null
            botTriggerDistanceCharacteristic = null
        }
    }

    override fun getGattCallback(): BleManagerGattCallback {
        super.getGattCallback()
        return SLBasePeripheralManagerGattCallback()
    }

    private fun initLedCharacteristics(service: BluetoothGattService) {
        ledStateCharacteristic      = service.getCharacteristic(LEGACY_LED_STATE_CHARACTERISTIC_UUID)
        ledBrightnessCharacteristic = service.getCharacteristic(LEGACY_LED_BRIGHTNESS_CHARACTERISTIC_UUID)
        ledTimeoutCharacteristic    = service.getCharacteristic(LEGACY_LED_TIMEOUT_CHARACTERISTIC_UUID)
    }

    private fun initAnimationCharacteristics(service: BluetoothGattService) {
        animationOnSpeedCharacteristic  = service.getCharacteristic(LEGACY_ANIMATION_ON_SPEED_CHARACTERISTIC_UUID)
    }

    private fun initSensorCharacteristics(service: BluetoothGattService) {
        topTriggerDistanceCharacteristic = service.getCharacteristic(LEGACY_TOP_SENSOR_TRIGGER_DISTANCE_CHARACTERISTIC_UUID)
        botTriggerDistanceCharacteristic = service.getCharacteristic(LEGACY_BOT_SENSOR_TRIGGER_DISTANCE_CHARACTERISTIC_UUID)
    }

    private val ledStateCallback: BooleanDataCallback = object : BooleanDataCallback() {
        override fun onBooleanReceived(device: BluetoothDevice, state: Boolean) {
            Log.e("TAG", "SLBaseManager led state: $state")
            ledState.postValue(state)
        }
    }

    private val ledBrightnessCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SLBaseManager led brightness: $data")
            ledBrightness.postValue(data.toFloat())
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            Log.e("TAG", "onInvalidDataReceived: $data" )
            super.onInvalidDataReceived(device, data)
        }
    }

    private val ledTimeoutCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SLBaseManager led timeout: $data")
            ledTimeout.postValue(data)
        }
    }

    private val animationOnSpeedCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SLBaseManager on speed: $data")
            animationOnSpeed.postValue(data.toFloat())
        }
    }

    private val topSensorTriggerDistanceCallback: DoubleByteDataCallback = object : DoubleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            topSensorTriggerDistance.postValue(data.toFloat())
            Log.e("TAG", "onIntegerValueReceived: TOP TRIGGER - $data" )
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: TOP SENSOR TRIGGER DISTANCE - $data" )
        }
    }

    private val botSensorTriggerDistanceCallback: DoubleByteDataCallback = object : DoubleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            botSensorTriggerDistance.postValue(data.toFloat())
            Log.e("TAG", "onIntegerValueReceived: BOT TRIGGER - $data" )
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: BOT SENSOR TRIGGER DISTANCE - $data")
        }
    }

    fun writeLedState(state: Boolean) {
        if (ledStateCharacteristic == null) return
        writeCharacteristic(
            ledStateCharacteristic,
            if (state) Data.opCode(0x01) else Data.opCode(0x00),
            WRITE_TYPE_NO_RESPONSE
        ).enqueue()
        ledState.postValue(state)
    }

    fun writeLedBrightness(brightness: Float) {
        Log.e("TAG", "writeLedBrightness: $brightness ")
        if (ledBrightnessCharacteristic == null) return
        writeCharacteristic(
            ledBrightnessCharacteristic,
            Data.opCode(brightness.toInt().toByte()),
            WRITE_TYPE_NO_RESPONSE
        ).enqueue()
        //ledBrightness.postValue(brightness)
    }

    fun writeLedTimeout(timeout: Int) {
        ledTimeoutCharacteristic?.let {
            writeCharacteristic(
                it,
                Data.opCode(timeout.toByte()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            ledTimeout.postValue(timeout)
        }
    }

    fun writeTopSensorTriggerDistance(distance: Float) {
        topTriggerDistanceCharacteristic?.let {
            writeCharacteristic(
                it,
                Data.opCode(distance.toInt().toByte()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            Log.e("TAG", "writeTopSensorTriggerDistance: ${createDoubleByteData(distance.toInt()).joinToString()}" )
            topSensorTriggerDistance.postValue(distance)
        }
    }

    fun writeBotSensorTriggerDistance(distance: Float) {
        botTriggerDistanceCharacteristic?.let {
            writeCharacteristic(
                it,
                Data.opCode(distance.toInt().toByte()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            Log.e("TAG", "writeBotSensorTriggerDistance: ${createDoubleByteData(distance.toInt()).joinToString()}" )
            botSensorTriggerDistance.postValue(distance)
        }
    }

    fun writeAnimationOnSpeed(speed: Float) {
        animationOnSpeedCharacteristic?.let {
            writeCharacteristic(
                it,
                Data.opCode(speed.toInt().toByte()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            Log.e("TAG", "writeAnimationOnSpeed: $speed")
            //animationOnSpeed.postValue(speed)
        }
    }

}