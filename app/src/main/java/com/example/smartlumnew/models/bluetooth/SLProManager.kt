package com.example.smartlumnew.models.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.lifecycle.MutableLiveData
import com.example.smartlumnew.models.data.*
import com.example.smartlumnew.models.data.peripheralData.SlProAdaptiveModes
import com.example.smartlumnew.models.data.peripheralData.SlProAnimations
import com.example.smartlumnew.models.data.peripheralData.SlProControllerType
import com.example.smartlumnew.models.data.peripheralData.SlProStairsWorkModes
import no.nordicsemi.android.ble.data.Data
import java.util.*

/**
 * Конкретная реализация менеджера.
 * Принцип абсолютно такой-же как и в родительком классе, просто дополняем его.
 */
class SLProManager(context: Context) : PeripheralManager(context) {

    /**
     * Рекламный UUID устройства SL-PRO. По идее не нужен, так как скинирование идет по маске
     */
    companion object {
        val SL_PRO_SERVICE_UUID: UUID = UUID.fromString("BB930004-3CE1-4720-A753-28C0159DC777")
    }

    private var primaryColorCharacteristic:              BluetoothGattCharacteristic? = null
    private var randomColorCharacteristic:               BluetoothGattCharacteristic? = null
    private var ledStateCharacteristic:                  BluetoothGattCharacteristic? = null
    private var ledBrightnessCharacteristic:             BluetoothGattCharacteristic? = null
    private var ledTimeoutCharacteristic:                BluetoothGattCharacteristic? = null
    private var animationModeCharacteristic:             BluetoothGattCharacteristic? = null
    private var animationOnSpeedCharacteristic:          BluetoothGattCharacteristic? = null
    private var controllerTypeCharacteristic:            BluetoothGattCharacteristic? = null
    private var adaptiveBrightnessCharacteristic:        BluetoothGattCharacteristic? = null
    private var stairsWorkModeCharacteristic:            BluetoothGattCharacteristic? = null
    private var stepsCountCharacteristic:                BluetoothGattCharacteristic? = null
    private var topSensorCountCharacteristic:            BluetoothGattCharacteristic? = null
    private var botSensorCountCharacteristic:            BluetoothGattCharacteristic? = null
    private var standbyLightingStateCharacteristic:      BluetoothGattCharacteristic? = null
    private var standbyLightingBrightnessCharacteristic: BluetoothGattCharacteristic? = null
    private var standbyLightingTopCountCharacteristic:   BluetoothGattCharacteristic? = null
    private var standbyLightingBotCountCharacteristic:   BluetoothGattCharacteristic? = null
    private var topTriggerDistanceCharacteristic:        BluetoothGattCharacteristic? = null
    private var botTriggerDistanceCharacteristic:        BluetoothGattCharacteristic? = null
    private var topTriggerLightnessCharacteristic:       BluetoothGattCharacteristic? = null
    private var botTriggerLightnessCharacteristic:       BluetoothGattCharacteristic? = null
    private var topCurrentDistanceCharacteristic:        BluetoothGattCharacteristic? = null
    private var botCurrentDistanceCharacteristic:        BluetoothGattCharacteristic? = null
    private var topCurrentLightnessCharacteristic:       BluetoothGattCharacteristic? = null
    private var botCurrentLightnessCharacteristic:       BluetoothGattCharacteristic? = null

    val primaryColor        = MutableLiveData<Int>()
    val randomColor         = MutableLiveData<Boolean>()
    val ledState            = MutableLiveData<Boolean>()
    val ledBrightness       = MutableLiveData<Float>()
    val ledTimeout          = MutableLiveData<Int>()
    val animationMode       = MutableLiveData<SlProAnimations>()
    val animationOnSpeed    = MutableLiveData<Float>()
    val controllerType      = MutableLiveData<SlProControllerType>()
    val adaptiveBrightness  = MutableLiveData<SlProAdaptiveModes>()
    val stairsWorkModes     = MutableLiveData<SlProStairsWorkModes>()
    val stepsCount          = MutableLiveData<Int>()
    val topSensorCount      = MutableLiveData<Int>()
    val botSensorCount      = MutableLiveData<Int>()
    val standbyState        = MutableLiveData<Boolean>()
    val standbyBrightness   = MutableLiveData<Float>()
    val standbyTopCount     = MutableLiveData<Int>()
    val standbyBotCount     = MutableLiveData<Int>()
    val topTriggerDistance  = MutableLiveData<Float>()
    val botTriggerDistance  = MutableLiveData<Float>()
    val topTriggerLightness = MutableLiveData<Float>()
    val botTriggerLightness = MutableLiveData<Float>()
    val topCurrentDistance  = MutableLiveData<Int>()
    val botCurrentDistance  = MutableLiveData<Int>()
    val topCurrentLightness = MutableLiveData<Int>()
    val botCurrentLightness = MutableLiveData<Int>()

    init {
        isInitialized.postValue(true)
    }

    /**
     * Переопределяем родительский класс, добавляем в него дополнительную реализацию
     * Не забываем вызвать super метод
     */
    private inner class SLBasePeripheralManagerGattCallback : PeripheralManagerGattCallback() {
        override fun initialize() {
            super.initialize()
            Log.e("TAG", "initialize SLPro manager: ")
            readCharacteristic(primaryColorCharacteristic).with(primaryColorCallback).enqueue()
            readCharacteristic(randomColorCharacteristic).with(randomColorCallBack).enqueue()
            readCharacteristic(ledStateCharacteristic).with(ledStateCallback).enqueue()
            readCharacteristic(ledBrightnessCharacteristic).with(ledBrightnessCallback).enqueue()
            readCharacteristic(ledTimeoutCharacteristic).with(ledTimeoutCallback).enqueue()
            readCharacteristic(animationModeCharacteristic).with(animationModeCallback).enqueue()
            readCharacteristic(animationOnSpeedCharacteristic).with(animationOnSpeedCallback).enqueue()
            readCharacteristic(controllerTypeCharacteristic).with(controllerTypeCallback).enqueue()
            readCharacteristic(adaptiveBrightnessCharacteristic).with(adaptiveBrightnessCallback).enqueue()
            readCharacteristic(stairsWorkModeCharacteristic).with(stairsWorkModeCallBack).enqueue()
            readCharacteristic(stepsCountCharacteristic).with(stepsCountCallback).enqueue()
            readCharacteristic(topSensorCountCharacteristic).with(topSensorCountCallback).enqueue()
            readCharacteristic(botSensorCountCharacteristic).with(botSensorCountCallback).enqueue()
            readCharacteristic(standbyLightingStateCharacteristic).with(standbyStateCallback).enqueue()
            readCharacteristic(standbyLightingBrightnessCharacteristic).with(standbyBrightnessCallback).enqueue()
            readCharacteristic(standbyLightingTopCountCharacteristic).with(standbyTopCountCallback).enqueue()
            readCharacteristic(standbyLightingBotCountCharacteristic).with(standbyBotCountCallback).enqueue()
            readCharacteristic(topTriggerDistanceCharacteristic).with(topSensorTriggerDistanceCallback).enqueue()
            readCharacteristic(botTriggerDistanceCharacteristic).with(botSensorTriggerDistanceCallback).enqueue()
            readCharacteristic(topTriggerLightnessCharacteristic).with(topSensorTriggerLightnessCallback).enqueue()
            readCharacteristic(botTriggerLightnessCharacteristic).with(botSensorTriggerLightnessCallback).enqueue()
            setNotificationCallback(topCurrentDistanceCharacteristic).with(topSensorCurrentDistanceCallback)
            readCharacteristic(topCurrentDistanceCharacteristic).with(topSensorCurrentDistanceCallback).enqueue()
            enableNotifications(topCurrentDistanceCharacteristic).enqueue()
            setNotificationCallback(botCurrentDistanceCharacteristic).with(botSensorCurrentDistanceCallback)
            readCharacteristic(botCurrentDistanceCharacteristic).with(botSensorCurrentDistanceCallback).enqueue()
            enableNotifications(botCurrentDistanceCharacteristic).enqueue()
            setNotificationCallback(topCurrentLightnessCharacteristic).with(topSensorCurrentLightnessCallback)
            readCharacteristic(topCurrentLightnessCharacteristic).with(topSensorCurrentLightnessCallback).enqueue()
            enableNotifications(topCurrentLightnessCharacteristic).enqueue()
            setNotificationCallback(botCurrentLightnessCharacteristic).with(botSensorCurrentLightnessCallback)
            readCharacteristic(botCurrentLightnessCharacteristic).with(botSensorCurrentLightnessCallback).enqueue()
            enableNotifications(botCurrentLightnessCharacteristic).enqueue()

        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            super.isRequiredServiceSupported(gatt)
            val colorService     = gatt.getService(COLOR_SERVICE_UUID)
            val ledService       = gatt.getService(LED_SERVICE_UUID)
            val animationService = gatt.getService(ANIMATION_SERVICE_UUID)
            val sensorService    = gatt.getService(SENSOR_SERVICE_UUID)
            val stairsService    = gatt.getService(STAIRS_SERVICE_UUID)
            colorService?.let     { initColorCharacteristics(it) }
            ledService?.let       { initLedCharacteristics(it) }
            animationService?.let { initAnimationCharacteristics(it) }
            sensorService?.let    { initSensorCharacteristics(it) }
            stairsService?.let    { initStairsCharacteristics(it) }
            return colorService != null &&
                    ledService != null &&
                    animationService != null &&
                    sensorService != null &&
                    stairsService != null
        }

        override fun onServicesInvalidated() {
            super.onServicesInvalidated()
            primaryColorCharacteristic              = null
            ledStateCharacteristic                  = null
            ledBrightnessCharacteristic             = null
            ledTimeoutCharacteristic                = null
            animationModeCharacteristic             = null
            animationOnSpeedCharacteristic          = null
            topTriggerDistanceCharacteristic        = null
            botTriggerDistanceCharacteristic        = null
            topCurrentDistanceCharacteristic        = null
            botCurrentDistanceCharacteristic        = null
            topTriggerLightnessCharacteristic       = null
            botTriggerLightnessCharacteristic       = null
            topCurrentLightnessCharacteristic       = null
            botCurrentLightnessCharacteristic       = null
            stepsCountCharacteristic                = null
            standbyLightingStateCharacteristic      = null
            standbyLightingTopCountCharacteristic   = null
            standbyLightingBotCountCharacteristic   = null
            standbyLightingBrightnessCharacteristic = null
        }
    }

    override fun getGattCallback(): BleManagerGattCallback {
        super.getGattCallback()
        return SLBasePeripheralManagerGattCallback()
    }

    private fun initColorCharacteristics(service: BluetoothGattService) {
        primaryColorCharacteristic = service.getCharacteristic(COLOR_PRIMARY_CHARACTERISTIC_UUID)
        randomColorCharacteristic  = service.getCharacteristic(COLOR_RANDOM_CHARACTERISTIC_UUID)
    }

    private fun initLedCharacteristics(service: BluetoothGattService) {
        ledStateCharacteristic           = service.getCharacteristic(LED_STATE_CHARACTERISTIC_UUID)
        ledBrightnessCharacteristic      = service.getCharacteristic(LED_BRIGHTNESS_CHARACTERISTIC_UUID)
        ledTimeoutCharacteristic         = service.getCharacteristic(LED_TIMEOUT_CHARACTERISTIC_UUID)
        controllerTypeCharacteristic     = service.getCharacteristic(LED_TYPE_CHARACTERISTIC_UUID)
        adaptiveBrightnessCharacteristic = service.getCharacteristic(LED_ADAPTIVE_MODE_CHARACTERISTIC_UUID)
    }

    private fun initAnimationCharacteristics(service: BluetoothGattService) {
        animationModeCharacteristic     = service.getCharacteristic(ANIMATION_MODE_CHARACTERISTIC_UUID)
        animationOnSpeedCharacteristic  = service.getCharacteristic(ANIMATION_ON_SPEED_CHARACTERISTIC_UUID)
    }

    private fun initSensorCharacteristics(service: BluetoothGattService) {
        topTriggerDistanceCharacteristic = service.getCharacteristic(TOP_SENSOR_TRIGGER_DISTANCE_CHARACTERISTIC_UUID)
        botTriggerDistanceCharacteristic = service.getCharacteristic(BOT_SENSOR_TRIGGER_DISTANCE_CHARACTERISTIC_UUID)
        topCurrentDistanceCharacteristic = service.getCharacteristic(TOP_SENSOR_CURRENT_DISTANCE_CHARACTERISTIC_UUID)
        botCurrentDistanceCharacteristic = service.getCharacteristic(BOT_SENSOR_CURRENT_DISTANCE_CHARACTERISTIC_UUID)
        topTriggerLightnessCharacteristic = service.getCharacteristic(TOP_SENSOR_TRIGGER_LIGHTNESS_CHARACTERISTIC_UUID)
        botTriggerLightnessCharacteristic = service.getCharacteristic(BOT_SENSOR_TRIGGER_LIGHTNESS_CHARACTERISTIC_UUID)
        topCurrentLightnessCharacteristic = service.getCharacteristic(TOP_SENSOR_CURRENT_LIGHTNESS_CHARACTERISTIC_UUID)
        botCurrentLightnessCharacteristic = service.getCharacteristic(BOT_SENSOR_CURRENT_LIGHTNESS_CHARACTERISTIC_UUID)
    }

    private fun initStairsCharacteristics(service: BluetoothGattService) {
        stepsCountCharacteristic                = service.getCharacteristic(STEPS_COUNT_CHARACTERISTIC_UUID)
        standbyLightingStateCharacteristic      = service.getCharacteristic(STANDBY_LIGHTING_STATE_CHARACTERISTIC_UUID)
        standbyLightingBrightnessCharacteristic = service.getCharacteristic(STANDBY_BRIGHTNESS_CHARACTERISTIC_UUID)
        standbyLightingTopCountCharacteristic   = service.getCharacteristic(STANDBY_TOP_COUNT_CHARACTERISTIC_UUID)
        standbyLightingBotCountCharacteristic   = service.getCharacteristic(STANDBY_BOT_COUNT_CHARACTERISTIC_UUID)
        stairsWorkModeCharacteristic            = service.getCharacteristic(STAIRS_WORK_MODE_CHARACTERISTIC_UUID)
        topSensorCountCharacteristic            = service.getCharacteristic(TOP_SENSOR_COUNT_CHARACTERISTIC_UUID)
        botSensorCountCharacteristic            = service.getCharacteristic(BOT_SENSOR_COUNT_CHARACTERISTIC_UUID)
    }

    private val primaryColorCallback: RGBDataCallback = object : RGBDataCallback() {
        override fun onRGBReceived(device: BluetoothDevice, color: Int) {
            Log.e("TAG", "SlProManager primary color: $color")
            primaryColor.postValue(color)
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: primaryColor $data" )
        }
    }

    private val randomColorCallBack: BooleanDataCallback = object : BooleanDataCallback() {
        override fun onBooleanReceived(device: BluetoothDevice, state: Boolean) {
            Log.e("TAG", "SlProManager random color: $state")
            randomColor.postValue(state)
        }
    }

    private val ledStateCallback: BooleanDataCallback = object : BooleanDataCallback() {
        override fun onBooleanReceived(device: BluetoothDevice, state: Boolean) {
            Log.e("TAG", "SlProManager led state: $state")
            ledState.postValue(state)
        }
    }

    private val ledBrightnessCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlProManager led brightness: $data")
            ledBrightness.postValue(data.toFloat())
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            Log.e("TAG", "onInvalidDataReceived: led brightness $data" )
            super.onInvalidDataReceived(device, data)
        }
    }

    private val ledTimeoutCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlProManager led timeout: $data")
            ledTimeout.postValue(data)
        }
    }

    private val controllerTypeCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlProManager led type: $data")
            controllerType.postValue(SlProControllerType.valueOf(data))
        }
    }

    private val adaptiveBrightnessCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlProManager adaptive mode: $data" )
            adaptiveBrightness.postValue(SlProAdaptiveModes.valueOf(data))
        }
    }

    private val stairsWorkModeCallBack: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlProManager: work mode $data")
            stairsWorkModes.postValue(SlProStairsWorkModes.valueOf(data))
        }
    }

    private val topSensorCountCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlProManager: top sensor count $data")
            topSensorCount.postValue(data)
        }
    }

    private val botSensorCountCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlProManager: bot sensor count $data")
            botSensorCount.postValue(data)
        }
    }

    private val animationModeCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlProManager animation mode: $data")
            animationMode.postValue(SlProAnimations.valueOf(data))
        }
    }

    private val animationOnSpeedCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlProManager on speed: $data")
            animationOnSpeed.postValue(data.toFloat())
        }
    }

    private val topSensorTriggerDistanceCallback: DoubleByteDataCallback = object : DoubleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            topTriggerDistance.postValue(data.toFloat())
            Log.e("TAG", "onIntegerValueReceived: top trigger distance - $data" )
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: top trigger distance - $data" )
        }
    }

    private val botSensorTriggerDistanceCallback: DoubleByteDataCallback = object : DoubleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            botTriggerDistance.postValue(data.toFloat())
            Log.e("TAG", "SlProManager: bot trigger distance- $data" )
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: bot trigger distance - $data")
        }
    }

    private val topSensorCurrentDistanceCallback: DoubleByteDataCallback = object : DoubleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            topCurrentDistance.postValue(data)
            Log.e("TAG", "onIntegerValueReceived: top current distance - $data" )
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: top current distance - $data" )
        }
    }

    private val botSensorCurrentDistanceCallback: DoubleByteDataCallback = object : DoubleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            botCurrentDistance.postValue(data)
            Log.e("TAG", "SlProManager: bot current distance- $data" )
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: bot current distance - $data")
        }
    }

    private val topSensorTriggerLightnessCallback: DoubleByteDataCallback = object : DoubleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            topTriggerLightness.postValue(data.toFloat())
            Log.e("TAG", "onIntegerValueReceived: top trigger lightness - $data" )
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: top trigger lightness - $data" )
        }
    }

    private val botSensorTriggerLightnessCallback: DoubleByteDataCallback = object : DoubleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            botTriggerLightness.postValue(data.toFloat())
            Log.e("TAG", "SlProManager: bot trigger lightness - $data" )
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: bot trigger lightness - $data")
        }
    }

    private val topSensorCurrentLightnessCallback: DoubleByteDataCallback = object : DoubleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            topCurrentLightness.postValue(data)
            Log.e("TAG", "onIntegerValueReceived: top current lightness - $data" )
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: top current lightness - $data" )
        }
    }

    private val botSensorCurrentLightnessCallback: DoubleByteDataCallback = object : DoubleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            botCurrentLightness.postValue(data)
            Log.e("TAG", "SlProManager: bot current lightness - $data" )
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: bot current lightness - $data")
        }
    }

    private val stepsCountCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlProManager: steps count - $data" )
            stepsCount.postValue(data)
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: steps count - $data")
        }
    }

    private val standbyTopCountCallback = object: SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlProManager: standby top count - $data" )
            standbyTopCount.postValue(data)
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: standby top count - $data")
        }
    }

    private val standbyBotCountCallback = object: SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlProManager: standby bot count - $data" )
            standbyBotCount.postValue(data)
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: standby bot count - $data")
        }
    }

    private val standbyStateCallback = object: BooleanDataCallback() {
        override fun onBooleanReceived(device: BluetoothDevice, state: Boolean) {
            Log.e("TAG", "SlProManager: standby state - $state" )
            standbyState.postValue(state)
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: standby state - $data")
        }
    }

    private val standbyBrightnessCallback = object: SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlProManager: standby brightness - $data" )
            standbyBrightness.postValue(data.toFloat())
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: standby brightness - $data")
        }
    }

    fun writePrimaryColor(color: Int) {
        primaryColorCharacteristic?.let {
            val data = byteArrayOf(
                Color.red(color).toByte(), Color.green(color)
                    .toByte(), Color.blue(color).toByte()
            )
            writeCharacteristic(
                it,
                data,
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
        }
        primaryColor.postValue(color)
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

    fun writeLedState(state: Boolean) {
        if (ledStateCharacteristic == null) return
        writeCharacteristic(
            ledStateCharacteristic,
            if (state) PeripheralData.setTrue() else PeripheralData.setFalse(),
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

    fun writeAnimationMode(mode: Int) {
        if (animationModeCharacteristic == null) return
        writeCharacteristic(
            animationModeCharacteristic,
            Data.opCode(mode.toByte()),
            WRITE_TYPE_NO_RESPONSE
        ).enqueue()
        animationMode.postValue(SlProAnimations.valueOf(mode))
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

    fun writeAdaptiveBrightnessMode(mode: Int) {
        adaptiveBrightnessCharacteristic?.let {
            writeCharacteristic(
                it,
                Data.opCode(mode.toByte()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            Log.e("TAG", "writeAdaptiveBrightnessMode: $mode" )
        }
        adaptiveBrightness.postValue(SlProAdaptiveModes.valueOf(mode))
    }

    fun writeStairsWorkMode(mode: Int) {
        stairsWorkModeCharacteristic?.let {
            writeCharacteristic(
                it,
                Data.opCode(mode.toByte()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            Log.e("TAG", "writeStairsWorkMode: $mode" )
        }
        stairsWorkModes.postValue(SlProStairsWorkModes.valueOf(mode))
    }

    fun writeStepsCount(count: Int) {
        stepsCountCharacteristic?.let {
            writeCharacteristic(
                it,
                Data.opCode(count.toByte()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            stepsCount.postValue(count)
        }
        stepsCount.postValue(count)
    }

    fun writeTopSensorsCount(count: Int) {
        topSensorCountCharacteristic?.let {
            writeCharacteristic(
                it,
                Data.opCode(count.toByte()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
        }
        topSensorCount.postValue(count)
    }

    fun writeBotSensorsCount(count: Int) {
        botSensorCountCharacteristic?.let {
            writeCharacteristic(
                it,
                Data.opCode(count.toByte()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
        }
        botSensorCount.postValue(count)
    }

    fun writeStandbyState(state: Boolean) {
        standbyLightingStateCharacteristic?.let {
            writeCharacteristic(
                it,
                if (state) PeripheralData.setTrue() else PeripheralData.setFalse(),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
        }
        standbyState.postValue(state)
    }

    fun writeStandbyBrightness(brightness: Float) {
        standbyLightingBrightnessCharacteristic?.let {
            writeCharacteristic(
                it,
                Data.opCode(brightness.toInt().toByte()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            standbyBrightness.postValue(brightness)
        }
    }

    fun writeStandbyTopCount(count: Int) {
        standbyLightingTopCountCharacteristic?.let {
            writeCharacteristic(
                it,
                Data.opCode(count.toByte()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            standbyTopCount.postValue(count)
        }
    }

    fun writeStandbyBotCount(count: Int) {
        standbyLightingBotCountCharacteristic?.let {
            writeCharacteristic(
                it,
                Data.opCode(count.toByte()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            standbyBotCount.postValue(count)
        }
    }

    fun writeTopSensorTriggerDistance(distance: Float) {
        topTriggerDistanceCharacteristic?.let {
            writeCharacteristic(
                it,
                createDoubleByteData(distance.toInt()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            Log.e("TAG", "writeTopSensorTriggerDistance: ${createDoubleByteData(distance.toInt()).joinToString()}" )
            topTriggerDistance.postValue(distance)
        }
    }

    fun writeBotSensorTriggerDistance(distance: Float) {
        botTriggerDistanceCharacteristic?.let {
            writeCharacteristic(
                it,
                createDoubleByteData(distance.toInt()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            Log.e("TAG", "writeBotSensorTriggerDistance: ${createDoubleByteData(distance.toInt()).joinToString()}" )
            botTriggerDistance.postValue(distance)
        }
    }

    fun writeTopSensorTriggerLightness(lightness: Float) {
        topTriggerLightnessCharacteristic?.let {
            writeCharacteristic(
                it,
                createDoubleByteData(lightness.toInt()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            Log.e("TAG", "writeTopSensorTriggerLightness: ${createDoubleByteData(lightness.toInt()).joinToString()}" )
            topTriggerLightness.postValue(lightness)
        }
    }

    fun writeBotSensorTriggerLightness(lightness: Float) {
        botTriggerLightnessCharacteristic?.let {
            writeCharacteristic(
                it,
                createDoubleByteData(lightness.toInt()),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            Log.e("TAG", "writeBotSensorTriggerLightness: ${createDoubleByteData(lightness.toInt()).joinToString()}" )
            botTriggerLightness.postValue(lightness)
        }
    }

}