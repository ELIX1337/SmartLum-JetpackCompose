package com.example.smartlumnew.models.viewModels

import android.app.Application
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import com.example.smartlumnew.models.bluetooth.SLProManager
import com.example.smartlumnew.models.data.peripheralData.SlProAdaptiveModes
import com.example.smartlumnew.models.data.peripheralData.SlProAnimations
import com.example.smartlumnew.models.data.peripheralData.SlProControllerType
import com.example.smartlumnew.models.data.peripheralData.SlProStairsWorkModes

/**
 * Конкретная ViewModel для устройства SL-PRO
 * Так же, как и менеджер, практически не имеет отличий от SL-STANDART, за исключением цвета
 */
class SLProViewModel(context: Application) : PeripheralViewModel(SLProManager(context)) {

    // Говорим о том, что у устройства есть расширенные настройки
    init {
        _hasOptions.postValue(true)
    }

    // Делаем апкаст менеджера
    private val manager: SLProManager = peripheralManager as SLProManager

    val primaryColor: LiveData<Int>    = manager.primaryColor
    val randomColor: LiveData<Boolean> = manager.randomColor
    val ledState: LiveData<Boolean>    = manager.ledState
    val ledBrightness: LiveData<Float> = manager.ledBrightness
    val ledTimeout: LiveData<Int>      = manager.ledTimeout
    val animationMode: LiveData<SlProAnimations> = manager.animationMode
    val animationOnSpeed: LiveData<Float> = manager.animationOnSpeed

    val controllerType: LiveData<SlProControllerType>    = manager.controllerType
    val adaptiveBrightness: LiveData<SlProAdaptiveModes> = manager.adaptiveBrightness
    val stairsWorkMode: LiveData<SlProStairsWorkModes>   = manager.stairsWorkModes
    val stepsCount: LiveData<Int> = manager.stepsCount
    val topSensorCount: LiveData<Int>   = manager.topSensorCount
    val botSensorCount: LiveData<Int>   = manager.botSensorCount
    val standbyState: LiveData<Boolean> = manager.standbyState
    val standbyBrightness: LiveData<Float> = manager.standbyBrightness
    val standbyTopCount: LiveData<Int>     = manager.standbyTopCount
    val standbyBotCount: LiveData<Int>     = manager.standbyBotCount
    val topTriggerDistance: LiveData<Float>  = manager.topTriggerDistance
    val botTriggerDistance: LiveData<Float>  = manager.botTriggerDistance
    val topTriggerLightness: LiveData<Float> = manager.topTriggerLightness
    val botTriggerLightness: LiveData<Float> = manager.botTriggerLightness
    val topCurrentDistance: LiveData<Int>    = manager.topCurrentDistance
    val botCurrentDistance: LiveData<Int>    = manager.botCurrentDistance
    val topCurrentLightness: LiveData<Int>   = manager.topCurrentLightness
    val botCurrentLightness: LiveData<Int>   = manager.botCurrentLightness

    private var initTopSensorTriggerDistance:  Float = 1.0f
    private var initBotSensorTriggerDistance:  Float = 1.0f
    private var initTopSensorTriggerLightness: Float = 1.0f
    private var initBotSensorTriggerLightness: Float = 1.0f
    private var initStepsCount: Int = 1

    fun setPrimaryColor(color: Int) {
        manager.writePrimaryColor(color)
    }

    fun setRandomColor(state: Boolean) {
        manager.writeRandomColor(state)
    }

    fun setLedBrightness(@FloatRange(from = 0.0) brightness: Float) {
        manager.writeLedBrightness(brightness)
    }

    fun setLedState(state: Boolean) {
        manager.writeLedState(state)
    }

    fun setLedTimeout(@IntRange(from = 0) timeout: Int) {
        manager.writeLedTimeout(timeout)
    }

    fun setAnimationMode(mode: SlProAnimations) {
        manager.writeAnimationMode(mode.code)
    }

    fun setAnimationOnSpeed(speed: Float) {
        manager.writeAnimationOnSpeed(speed)
    }

    fun initStepsCount(count: Int) {
        initStepsCount = count
    }

    fun initTopSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        initTopSensorTriggerDistance = distance
    }

    fun initBotSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        initBotSensorTriggerDistance = distance
    }

    fun initTopSensorTriggerLightness(value: Float) {
        initTopSensorTriggerLightness = value
    }

    fun initBotSensorTriggerLightness(value: Float) {
        initBotSensorTriggerLightness = value
    }

    fun setTopSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        manager.writeTopSensorTriggerDistance(distance)
    }

    fun setBotSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        manager.writeBotSensorTriggerDistance(distance)
    }

    fun setTopSensorTriggerLightness(value: Float) {
        manager.writeTopSensorTriggerLightness(value)
    }

    fun setBotSensorTriggerLightness(value: Float) {
        manager.writeBotSensorTriggerLightness(value)
    }

    fun setStepsCount(count: Int) {
        manager.writeStepsCount(count)
    }

    fun setStandbyState(state: Boolean) {
        manager.writeStandbyState(state)
    }

    fun setStandbyTopCount(count: Int) {
        manager.writeStandbyTopCount(count)
    }

    fun setStandbyBotCount(count: Int) {
        manager.writeStandbyBotCount(count)
    }

    fun setStandbyBrightness(brightness: Float) {
        manager.writeStandbyBrightness(brightness)
    }

    fun setAdaptiveMode(mode: SlProAdaptiveModes) {
        manager.writeAdaptiveBrightnessMode(mode.code)
    }

    fun setStairsWorkMode(mode: SlProStairsWorkModes) {
        manager.writeStairsWorkMode(mode.code)
    }

    fun setTopSensorsCount(count: Int) {
        manager.writeTopSensorsCount(count)
    }

    fun setBotSensorsCount(count: Int) {
        manager.writeBotSensorsCount(count)
    }

    // Этот метод сработает при нажатии кнопки "Подтвердить" на экране первичной настройки (инициализации)
    // Отправит все необходимые для настройки данные на устройство
    override fun commit() {
        manager.writeTopSensorTriggerDistance(initTopSensorTriggerDistance)
        manager.writeBotSensorTriggerDistance(initBotSensorTriggerDistance)
        manager.writeTopSensorTriggerLightness(initTopSensorTriggerLightness)
        manager.writeBotSensorTriggerLightness(initBotSensorTriggerLightness)
        manager.writeStepsCount(initStepsCount)
    }

}