package com.example.smartlumnew.models.viewModels

import android.app.Application
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartlumnew.models.bluetooth.SLBaseManager
import com.example.smartlumnew.models.data.PeripheralProfileEnum

/**
 * Конкретная ViewModel для устройства SL-BASE
 */
class SLBaseViewModel(context: Application) : PeripheralViewModel(SLBaseManager(context)) {

    // Говорим о том, что у устройства есть расширенные настройки
    init {
        _hasOptions.postValue(true)
    }

    // Делаем апкаст менеджера
    private val baseManager: SLBaseManager = peripheralManager as SLBaseManager

    val ledBrightness:            MutableLiveData<Float> = baseManager.ledBrightness
    val ledState:                 LiveData<Boolean> = baseManager.ledState
    val ledTimeout:               LiveData<Int>     = baseManager.ledTimeout
    val animationOnSpeed:         LiveData<Float>   = baseManager.animationOnSpeed
    val topSensorTriggerDistance: LiveData<Float>   = baseManager.topSensorTriggerDistance
    val botSensorTriggerDistance: LiveData<Float>   = baseManager.botSensorTriggerDistance

    var initTopSensorTriggerDistance: Float? = null
    var initBotSensorTriggerDistance: Float? = null

    fun setLedBrightness(@FloatRange(from = 0.0) brightness: Float) {
        baseManager.writeLedBrightness(brightness)
    }

    fun setLedState(state: Boolean) {
        baseManager.writeLedState(state)
    }

    fun setLedTimeout(@IntRange(from = 0) timeout: Int) {
        baseManager.writeLedTimeout(timeout)
    }

    fun initTopSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        initTopSensorTriggerDistance = distance
    }

    fun initBotSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        initBotSensorTriggerDistance = distance
    }

    fun setTopSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        baseManager.writeTopSensorTriggerDistance(distance)
    }

    fun setBotSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        baseManager.writeBotSensorTriggerDistance(distance)
    }

    fun setAnimationOnSpeed(speed: Float) {
        baseManager.writeAnimationOnSpeed(speed)
    }

    override fun isInitDataReady(): Boolean {
        return initTopSensorTriggerDistance != null &&
                initBotSensorTriggerDistance != null

    }

    // Этот метод сработает при нажатии кнопки "Подтвердить" на экране первичной настройки (инициализации)
    // Отправит все необходимые для настройки данные на устройство
    override fun commit(): Boolean {
        if (isInitDataReady()) {
            baseManager.writeTopSensorTriggerDistance(initTopSensorTriggerDistance!!)
            baseManager.writeBotSensorTriggerDistance(initBotSensorTriggerDistance!!)

            return true
        }
        return false
    }

}