package com.example.smartlumnew.models.viewModels

import android.app.Application
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartlumnew.models.bluetooth.SLBaseManager

class SLBaseViewModelFactory(private val context: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SLBaseViewModel::class.java)) {
            return SLBaseViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SLBaseViewModel(context: Application) : PeripheralViewModel(SLBaseManager(context)) {

    private val baseManager: SLBaseManager = peripheralManager as SLBaseManager

    val ledBrightness: MutableLiveData<Float> = baseManager.ledBrightness
    val ledState:                 LiveData<Boolean> = baseManager.ledState
    val ledTimeout:               LiveData<Int>     = baseManager.ledTimeout
    val animationOnSpeed:         LiveData<Float>   = baseManager.animationOnSpeed
    val topSensorTriggerDistance: LiveData<Float>     = baseManager.topSensorTriggerDistance
    val botSensorTriggerDistance: LiveData<Float>     = baseManager.botSensorTriggerDistance

    var initTopSensorTriggerDistance: Float = 1.0f
    var initBotSensorTriggerDistance: Float = 1.0f

    fun setLedBrightness(@FloatRange(from = 0.0) brightness: Float) {
        baseManager.writeLedBrightness(brightness)
    }

    fun setLedState(state: Boolean) {
        baseManager.writeLedState(state)
    }

    fun setLedTimeout(@IntRange(from = 0) timeout: Int) {
        baseManager.writeLedTimeout(timeout)
    }

    fun setTopSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        initTopSensorTriggerDistance = distance
    }

    fun setBotSensorTriggerDistance(@FloatRange(from = 1.0) distance: Float) {
        initBotSensorTriggerDistance = distance
    }

    fun setAnimationOnSpeed(speed: Float) {
        baseManager.writeAnimationOnSpeed(speed)
    }

    override fun commit() {
        baseManager.writeTopSensorTriggerDistance(initTopSensorTriggerDistance)
        baseManager.writeBotSensorTriggerDistance(initBotSensorTriggerDistance)
    }

}