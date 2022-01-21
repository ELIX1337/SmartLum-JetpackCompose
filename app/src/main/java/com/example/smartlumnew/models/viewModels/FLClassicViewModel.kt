package com.example.smartlumnew.models.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartlumnew.models.bluetooth.FLClassicManager
import com.example.smartlumnew.models.data.peripheralData.FlClassicAnimations
import com.example.smartlumnew.models.data.peripheralData.FlClassicAnimationDirections

class TorchereViewModelFactory(private val context: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FLClassicViewModel::class.java)) {
            return FLClassicViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class FLClassicViewModel(context: Application) : PeripheralViewModel(FLClassicManager(context)) {

    init {
        _hasOptions.postValue(false)
    }

    private val FLClassicManager: FLClassicManager = peripheralManager as FLClassicManager

    val primaryColor:       LiveData<Int> = FLClassicManager.primaryColor
    val secondaryColor:     LiveData<Int> = FLClassicManager.secondaryColor
    val randomColor:        LiveData<Boolean> = FLClassicManager.randomColor
    val animationMode:      LiveData<FlClassicAnimations> = FLClassicManager.animationMode
    val animationOnSpeed:   LiveData<Float> = FLClassicManager.animationOnSpeed
    val animationDirection: LiveData<FlClassicAnimationDirections> = FLClassicManager.animationDirection
    val animationStep:      LiveData<Int> = FLClassicManager.animationStep

    fun setPrimaryColor(color: Int) {
        FLClassicManager.writePrimaryColor(color)
    }

    fun setSecondaryColor(color: Int) {
        FLClassicManager.writeSecondaryColor(color)
    }

    fun setRandomColor(state: Boolean) {
        FLClassicManager.writeRandomColor(state)
    }

    fun setAnimationMode(mode: Int) {
        FLClassicManager.writeAnimationMode(mode)
    }

    fun setAnimationMode(mode: FlClassicAnimations) {
        FLClassicManager.writeAnimationMode(mode.code)
    }

    fun setAnimationOnSpeed(speed: Float) {
        FLClassicManager.writeAnimationOnSpeed(speed)
    }

    fun setAnimationOffSpeed(speed: Int) {
        FLClassicManager.writeAnimationOffSpeed(speed)
    }

    fun setAnimationDirection(direction: Int) {
        FLClassicManager.writeAnimationDirection(direction)
    }

    fun setAnimationDirection(direction: FlClassicAnimationDirections) {
        FLClassicManager.writeAnimationDirection(direction.code)
    }

    fun setAnimationStep(step: Int) {
        FLClassicManager.writeAnimationStep(step)
    }
}