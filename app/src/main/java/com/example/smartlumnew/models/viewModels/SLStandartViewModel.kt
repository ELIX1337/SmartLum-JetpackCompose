package com.example.smartlumnew.models.viewModels

import android.app.Application
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import com.example.smartlumnew.models.bluetooth.SLStandartManager
import com.example.smartlumnew.models.data.SlStandartAnimations

class SLStandartViewModel(context: Application) : PeripheralViewModel(SLStandartManager(context)) {

    init {
        _hasOptions.postValue(true)
    }

    private val manager: SLStandartManager = peripheralManager as SLStandartManager

    val primaryColor: LiveData<Int>    = manager.primaryColor
    val randomColor: LiveData<Boolean> = manager.randomColor
    val ledState: LiveData<Boolean>    = manager.ledState
    val ledBrightness: LiveData<Float> = manager.ledBrightness
    val ledTimeout: LiveData<Int>      = manager.ledTimeout
    val ledType: LiveData<Int>         = manager.ledType
    val animationMode: LiveData<SlStandartAnimations> = manager.animationMode
    val animationOnSpeed: LiveData<Float>    = manager.animationOnSpeed
    val topTriggerDistance: LiveData<Float>  = manager.topTriggerDistance
    val botTriggerDistance: LiveData<Float>  = manager.botTriggerDistance
    val topCurrentDistance: LiveData<Int>    = manager.topCurrentDistance
    val botCurrentDistance: LiveData<Int>    = manager.botCurrentDistance
    val topTriggerLightness: LiveData<Float> = manager.topTriggerLightness
    val botTriggerLightness: LiveData<Float> = manager.botTriggerLightness
    val topCurrentLightness: LiveData<Int>   = manager.topCurrentLightness
    val botCurrentLightness: LiveData<Int>   = manager.botCurrentLightness
    val stepsCount: LiveData<Int>            = manager.stepsCount
    val standbyState: LiveData<Boolean>      = manager.standbyState
    val standbyTopCount: LiveData<Int>       = manager.standbyTopCount
    val standbyBotCount: LiveData<Int>       = manager.standbyBotCount
    val standbyBrightness: LiveData<Float>   = manager.standbyBrightness

    private var initTopSensorTriggerDistance:  Float = 1.0f
    private var initBotSensorTriggerDistance:  Float = 1.0f
    private var initTopSensorTriggerLightness: Float = 1.0f
    private var initBotSensorTriggerLightness: Float = 1.0f

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

    fun setAnimationMode(mode: SlStandartAnimations) {
        manager.writeAnimationMode(mode.code)
    }

    fun setAnimationOnSpeed(speed: Float) {
        manager.writeAnimationOnSpeed(speed)
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

    override fun commit() {
        manager.writeTopSensorTriggerDistance(initTopSensorTriggerDistance)
        manager.writeBotSensorTriggerDistance(initBotSensorTriggerDistance)
        manager.writeTopSensorTriggerLightness(initTopSensorTriggerLightness)
        manager.writeBotSensorTriggerLightness(initBotSensorTriggerLightness)
    }

}