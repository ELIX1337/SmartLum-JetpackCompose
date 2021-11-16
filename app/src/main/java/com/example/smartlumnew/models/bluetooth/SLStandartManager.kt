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
import com.example.smartlumnew.models.data.PeripheralData
import com.example.smartlumnew.models.data.SlStandartAnimations
import com.example.smartlumnew.models.data.createDoubleByteData
import no.nordicsemi.android.ble.data.Data
import java.util.*

class SLStandartManager(context: Context) : PeripheralManager(context) {

    /** SL_STANDART UUID  */
    companion object {
        val SL_STANDART_SERVICE_UUID: UUID = UUID.fromString("BB930004-3CE1-4720-A753-28C0159DC777")
    }

    private var primaryColorCharacteristic:              BluetoothGattCharacteristic? = null
    private var randomColorCharacteristic:               BluetoothGattCharacteristic? = null
    private var ledStateCharacteristic:                  BluetoothGattCharacteristic? = null
    private var ledBrightnessCharacteristic:             BluetoothGattCharacteristic? = null
    private var ledTimeoutCharacteristic:                BluetoothGattCharacteristic? = null
    private var ledTypeCharacteristic:                   BluetoothGattCharacteristic? = null
    private var animationModeCharacteristic:             BluetoothGattCharacteristic? = null
    private var animationOnSpeedCharacteristic:          BluetoothGattCharacteristic? = null
    private var topTriggerDistanceCharacteristic:        BluetoothGattCharacteristic? = null
    private var botTriggerDistanceCharacteristic:        BluetoothGattCharacteristic? = null
    private var topCurrentDistanceCharacteristic:        BluetoothGattCharacteristic? = null
    private var botCurrentDistanceCharacteristic:        BluetoothGattCharacteristic? = null
    private var topTriggerLightnessCharacteristic:       BluetoothGattCharacteristic? = null
    private var botTriggerLightnessCharacteristic:       BluetoothGattCharacteristic? = null
    private var topCurrentLightnessCharacteristic:       BluetoothGattCharacteristic? = null
    private var botCurrentLightnessCharacteristic:       BluetoothGattCharacteristic? = null
    private var stepsCountCharacteristic:                BluetoothGattCharacteristic? = null
    private var standbyLightingStateCharacteristic:      BluetoothGattCharacteristic? = null
    private var standbyLightingTopCountCharacteristic:   BluetoothGattCharacteristic? = null
    private var standbyLightingBotCountCharacteristic:   BluetoothGattCharacteristic? = null
    private var standbyLightingBrightnessCharacteristic: BluetoothGattCharacteristic? = null

    val primaryColor        = MutableLiveData<@IntRange(from = 0)Int>()
    val randomColor         = MutableLiveData<Boolean>()
    val ledState            = MutableLiveData<Boolean>()
    val ledBrightness       = MutableLiveData<Float>()
    val ledTimeout          = MutableLiveData<@IntRange(from = 1)Int>()
    val ledType             = MutableLiveData<Int>()
    val animationMode       = MutableLiveData<SlStandartAnimations>()
    val animationOnSpeed    = MutableLiveData<@FloatRange(from = 1.0)Float>()
    val topTriggerDistance  = MutableLiveData<@FloatRange(from = 1.0)Float>()
    val botTriggerDistance  = MutableLiveData<@FloatRange(from = 1.0)Float>()
    val topCurrentDistance  = MutableLiveData<Int>()
    val botCurrentDistance  = MutableLiveData<Int>()
    val topTriggerLightness = MutableLiveData<Float>()
    val botTriggerLightness = MutableLiveData<Float>()
    val topCurrentLightness = MutableLiveData<Int>()
    val botCurrentLightness = MutableLiveData<Int>()
    val stepsCount          = MutableLiveData<Int>()
    val standbyState        = MutableLiveData<Boolean>()
    val standbyTopCount     = MutableLiveData<Int>()
    val standbyBotCount     = MutableLiveData<Int>()
    val standbyBrightness   = MutableLiveData<Float>()

    private inner class SLBasePeripheralManagerGattCallback : PeripheralManagerGattCallback() {
        override fun initialize() {
            super.initialize()
            Log.e("TAG", "initialize SLStandart manager: ")
            readCharacteristic(primaryColorCharacteristic).with(primaryColorCallback).enqueue()
            readCharacteristic(randomColorCharacteristic).with(randomColorCallBack).enqueue()
            readCharacteristic(ledStateCharacteristic).with(ledStateCallback).enqueue()
            readCharacteristic(ledBrightnessCharacteristic).with(ledBrightnessCallback).enqueue()
            readCharacteristic(ledTimeoutCharacteristic).with(ledTimeoutCallback).enqueue()
            readCharacteristic(ledTypeCharacteristic).with(ledTypeCallback).enqueue()
            readCharacteristic(animationModeCharacteristic).with(animationModeCallback).enqueue()
            readCharacteristic(animationOnSpeedCharacteristic).with(animationOnSpeedCallback).enqueue()
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
            readCharacteristic(stepsCountCharacteristic).with(stepsCountCallback).enqueue()
            readCharacteristic(standbyLightingStateCharacteristic).with(standbyStateCallback).enqueue()
            readCharacteristic(standbyLightingTopCountCharacteristic).with(standbyTopCountCallback).enqueue()
            readCharacteristic(standbyLightingBotCountCharacteristic).with(standbyBotCountCallback).enqueue()
            readCharacteristic(standbyLightingBrightnessCharacteristic).with(standbyBrightnessCallback).enqueue()
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
    }

    private fun initLedCharacteristics(service: BluetoothGattService) {
        ledStateCharacteristic      = service.getCharacteristic(LEGACY_LED_STATE_CHARACTERISTIC_UUID)
        ledBrightnessCharacteristic = service.getCharacteristic(LED_BRIGHTNESS_CHARACTERISTIC_UUID)
        ledTimeoutCharacteristic    = service.getCharacteristic(LEGACY_LED_TIMEOUT_CHARACTERISTIC_UUID)
    }

    private fun initAnimationCharacteristics(service: BluetoothGattService) {
        animationOnSpeedCharacteristic  = service.getCharacteristic(LEGACY_ANIMATION_ON_SPEED_CHARACTERISTIC_UUID)
    }

    private fun initSensorCharacteristics(service: BluetoothGattService) {
        topTriggerDistanceCharacteristic = service.getCharacteristic(LEGACY_TOP_SENSOR_TRIGGER_DISTANCE_CHARACTERISTIC_UUID)
        botTriggerDistanceCharacteristic = service.getCharacteristic(LEGACY_BOT_SENSOR_TRIGGER_DISTANCE_CHARACTERISTIC_UUID)
    }

    private fun initStairsCharacteristics(service: BluetoothGattService) {
        stepsCountCharacteristic                = service.getCharacteristic(STEPS_COUNT_CHARACTERISTIC_UUID)
        standbyLightingStateCharacteristic      = service.getCharacteristic(STANDBY_LIGHTING_STATE_CHARACTERISTIC_UUID)
        standbyLightingTopCountCharacteristic   = service.getCharacteristic(STANDBY_TOP_COUNT_CHARACTERISTIC_UUID)
        standbyLightingBotCountCharacteristic   = service.getCharacteristic(LEGACY_STANDBY_BOT_COUNT_CHARACTERISTIC_UUID)
        standbyLightingBrightnessCharacteristic = service.getCharacteristic(STANDBY_BRIGHTNESS_CHARACTERISTIC_UUID)
    }

    private val primaryColorCallback: RGBDataCallback = object : RGBDataCallback() {
        override fun onRGBReceived(device: BluetoothDevice, color: Int) {
            Log.e("TAG", "SlStandartManager primary color: $color")
            primaryColor.postValue(color)
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: primaryColor $data" )
        }
    }

    private val randomColorCallBack: BooleanDataCallback = object : BooleanDataCallback() {
        override fun onBooleanReceived(device: BluetoothDevice, state: Boolean) {
            Log.e("TAG", "SlStandartManager random color: $state")
            randomColor.postValue(state)
        }
    }

    private val ledStateCallback: BooleanDataCallback = object : BooleanDataCallback() {
        override fun onBooleanReceived(device: BluetoothDevice, state: Boolean) {
            Log.e("TAG", "SlStandartManager led state: $state")
            ledState.postValue(state)
        }
    }

    private val ledBrightnessCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlStandartManager led brightness: $data")
            ledBrightness.postValue(data.toFloat())
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            Log.e("TAG", "onInvalidDataReceived: led brightness $data" )
            super.onInvalidDataReceived(device, data)
        }
    }

    private val ledTimeoutCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlStandartManager led timeout: $data")
            ledTimeout.postValue(data)
        }
    }

    private val ledTypeCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlStandartManager led type: $data")
            ledType.postValue(data)
        }
    }

    private val animationModeCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlStandartManager animation mode: $data")
            animationMode.postValue(SlStandartAnimations.valueOf(data))
        }
    }

    private val animationOnSpeedCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlStandartManager on speed: $data")
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
            Log.e("TAG", "SlStandartManager: bot trigger distance- $data" )
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
            Log.e("TAG", "SlStandartManager: bot current distance- $data" )
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
            Log.e("TAG", "SlStandartManager: bot trigger lightness - $data" )
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
            Log.e("TAG", "SlStandartManager: bot current lightness - $data" )
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: bot current lightness - $data")
        }
    }

    private val stepsCountCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlStandartManager: steps count - $data" )
            stepsCount.postValue(data)
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: steps count - $data")
        }
    }

    private val standbyTopCountCallback = object: SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlStandartManager: standby top count - $data" )
            standbyTopCount.postValue(data)
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: standby top count - $data")
        }
    }

    private val standbyBotCountCallback = object: SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlStandartManager: standby bot count - $data" )
            standbyBotCount.postValue(data)
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: standby bot count - $data")
        }
    }

    private val standbyStateCallback = object: BooleanDataCallback() {
        override fun onBooleanReceived(device: BluetoothDevice, state: Boolean) {
            Log.e("TAG", "SlStandartManager: standby state - $state" )
            standbyState.postValue(state)
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: standby state - $data")
        }
    }

    private val standbyBrightnessCallback = object: SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "SlStandartManager: standby brightness - $data" )
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

    fun writeAnimationMode(mode: Int) {
        if (animationModeCharacteristic == null) return
        writeCharacteristic(
            animationModeCharacteristic,
            Data.opCode(mode.toByte()),
            WRITE_TYPE_NO_RESPONSE
        ).enqueue()
        animationMode.postValue(SlStandartAnimations.valueOf(mode))
    }

    fun writeAnimationOnSpeed(speed: Float) {
        animationOnSpeedCharacteristic?.let {
            writeCharacteristic(
                it,
                Data.opCode(speed.toInt().toByte()),
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            Log.e("TAG", "writeAnimationOnSpeed: $speed")
            //animationOnSpeed.postValue(speed)
        }
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


}