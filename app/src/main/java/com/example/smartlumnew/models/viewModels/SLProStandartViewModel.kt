package com.example.smartlumnew.models.viewModels

import android.app.Application
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import com.example.smartlumnew.models.bluetooth.SLProStandartManager
import com.example.smartlumnew.models.data.PeripheralProfileEnum
import com.example.smartlumnew.models.data.peripheralData.SlProAdaptiveModes
import com.example.smartlumnew.models.data.peripheralData.SlProAnimations
import com.example.smartlumnew.models.data.peripheralData.SlProStandartControllerType
import com.example.smartlumnew.models.data.peripheralData.SlProStairsWorkModes

/**
 * ViewModel для устройств SL-PRO и SL-STANDART.
 * Оба устройства программно и функционально идентичны, различия лишь в данных (для нас).
 * Это позволяет использовать одну ViewModel и PeripheralManager на оба устройства.
 */
class SLProStandartViewModel(context: Application) : PeripheralViewModel(SLProStandartManager(context)) {

    // Говорим о том, что у устройства есть расширенные настройки
    init {
        _hasOptions.postValue(true)
    }

    // Делаем апкаст менеджера
    private val proStandartManager: SLProStandartManager = peripheralManager as SLProStandartManager

    val primaryColor: LiveData<Int>    = proStandartManager.primaryColor
    val randomColor: LiveData<Boolean> = proStandartManager.randomColor
    val ledState: LiveData<Boolean>    = proStandartManager.ledState
    val ledBrightness: LiveData<Float> = proStandartManager.ledBrightness
    val ledTimeout: LiveData<Int>      = proStandartManager.ledTimeout
    val animationMode: LiveData<SlProAnimations> = proStandartManager.animationMode
    val animationOnSpeed: LiveData<Float> = proStandartManager.animationOnSpeed

    val controllerType: LiveData<SlProStandartControllerType>    = proStandartManager.controllerType
    val adaptiveBrightness: LiveData<SlProAdaptiveModes> = proStandartManager.adaptiveBrightness
    val stairsWorkMode: LiveData<SlProStairsWorkModes>   = proStandartManager.stairsWorkModes
    val stepsCount: LiveData<Int> = proStandartManager.stepsCount
    val topSensorCount: LiveData<Int>   = proStandartManager.topSensorCount
    val botSensorCount: LiveData<Int>   = proStandartManager.botSensorCount
    val standbyState: LiveData<Boolean> = proStandartManager.standbyState
    val standbyBrightness: LiveData<Float> = proStandartManager.standbyBrightness
    val standbyTopCount: LiveData<Int>     = proStandartManager.standbyTopCount
    val standbyBotCount: LiveData<Int>     = proStandartManager.standbyBotCount
    val topTriggerDistance: LiveData<Float>  = proStandartManager.topTriggerDistance
    val botTriggerDistance: LiveData<Float>  = proStandartManager.botTriggerDistance
    val topCurrentDistance: LiveData<Int>  = proStandartManager.topCurrentDistance
    val botCurrentDistance: LiveData<Int>  = proStandartManager.botCurrentDistance
    val topTriggerLightness: LiveData<Float> = proStandartManager.topTriggerLightness
    val botTriggerLightness: LiveData<Float> = proStandartManager.botTriggerLightness
    val topCurrentLightness: LiveData<Int>   = proStandartManager.topCurrentLightness
    val botCurrentLightness: LiveData<Int>   = proStandartManager.botCurrentLightness

    private var initTopSensorTriggerDistance:  Float? = null
    private var initBotSensorTriggerDistance:  Float? = null
    private var initStepsCount: Int? = null
    // Для SL-Pro
    private var initTopSensorCount: Int? = null
    private var initBotSensorCount: Int? = null

    fun setPrimaryColor(color: Int) {
        proStandartManager.writePrimaryColor(color)
    }

    fun setRandomColor(state: Boolean) {
        proStandartManager.writeRandomColor(state)
    }

    fun setLedBrightness(@FloatRange(from = 0.0) brightness: Float) {
        proStandartManager.writeLedBrightness(brightness)
    }

    fun setLedState(state: Boolean) {
        proStandartManager.writeLedState(state)
    }

    fun setLedTimeout(@IntRange(from = 0) timeout: Int) {
        proStandartManager.writeLedTimeout(timeout)
    }

    fun setAnimationMode(mode: SlProAnimations) {
        proStandartManager.writeAnimationMode(mode.code)
    }

    fun setAnimationOnSpeed(speed: Float) {
        proStandartManager.writeAnimationOnSpeed(speed)
    }

    fun setTopSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        proStandartManager.writeTopSensorTriggerDistance(distance)
    }

    fun setBotSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        proStandartManager.writeBotSensorTriggerDistance(distance)
    }

    fun setTopSensorTriggerLightness(value: Float) {
        proStandartManager.writeTopSensorTriggerLightness(value)
    }

    fun setBotSensorTriggerLightness(value: Float) {
        proStandartManager.writeBotSensorTriggerLightness(value)
    }

    fun setStepsCount(count: Int) {
        proStandartManager.writeStepsCount(count)
    }

    fun setStandbyState(state: Boolean) {
        proStandartManager.writeStandbyState(state)
    }

    fun setStandbyTopCount(count: Int) {
        proStandartManager.writeStandbyTopCount(count)
    }

    fun setStandbyBotCount(count: Int) {
        proStandartManager.writeStandbyBotCount(count)
    }

    fun setStandbyBrightness(brightness: Float) {
        proStandartManager.writeStandbyBrightness(brightness)
    }

    fun setAdaptiveMode(mode: SlProAdaptiveModes) {
        proStandartManager.writeAdaptiveBrightnessMode(mode.code)
    }

    fun setStairsWorkMode(mode: SlProStairsWorkModes) {
        proStandartManager.writeStairsWorkMode(mode.code)
    }

    fun setTopSensorsCount(count: Int) {
        proStandartManager.writeTopSensorsCount(count)
    }

    fun setBotSensorsCount(count: Int) {
        proStandartManager.writeBotSensorsCount(count)
    }

    fun initStepsCount(count: Int) {
        initStepsCount = count
    }

    fun initTopSensorCount(count: Int) {
        initTopSensorCount = count
    }

    fun initBotSensorCount(count: Int) {
        initBotSensorCount = count
    }

    fun initTopSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        initTopSensorTriggerDistance = distance
    }

    fun initBotSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        initBotSensorTriggerDistance = distance
    }

    override fun isInitDataReady(): Boolean {
        return if (peripheralType == PeripheralProfileEnum.SL_PRO) {
            initTopSensorTriggerDistance != null &&
                    initBotSensorTriggerDistance != null &&
                    initStepsCount != null &&
                    initTopSensorCount != null &&
                    initBotSensorCount != null
        } else {
            initTopSensorTriggerDistance != null &&
                    initBotSensorTriggerDistance != null &&
                    initStepsCount != null
        }

    }

    // Этот метод сработает при нажатии кнопки "Подтвердить" на экране первичной настройки (инициализации)
    // Отправит все необходимые для настройки данные на устройство
    override fun commit(): Boolean {
        if (isInitDataReady()) {
            proStandartManager.writeTopSensorTriggerDistance(initTopSensorTriggerDistance!!)
            proStandartManager.writeBotSensorTriggerDistance(initBotSensorTriggerDistance!!)
            proStandartManager.writeStepsCount(initStepsCount!!)

            // Дефолтные значения для SL-Standart
            // Захардкодил, по идее лучше сделать как на iOS
            proStandartManager.writeTopSensorsCount(initTopSensorCount ?: 1)
            proStandartManager.writeBotSensorsCount(initBotSensorCount ?: 1)

            return true
        }
        return false
    }

}