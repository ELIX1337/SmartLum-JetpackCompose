package com.example.smartlumnew.models.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartlumnew.models.bluetooth.TorchereManager
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

class FLClassicViewModel(context: Application) : PeripheralViewModel(TorchereManager(context)) {

    init {
        _hasOptions.postValue(false)
    }

    private val torchereManager: TorchereManager = peripheralManager as TorchereManager

    val primaryColor:       LiveData<Int> = torchereManager.primaryColor
    val secondaryColor:     LiveData<Int> = torchereManager.secondaryColor
    val randomColor:        LiveData<Boolean> = torchereManager.randomColor
    val animationMode:      LiveData<FlClassicAnimations> = torchereManager.animationMode
    val animationOnSpeed:   LiveData<Float> = torchereManager.animationOnSpeed
    val animationDirection: LiveData<FlClassicAnimationDirections> = torchereManager.animationDirection
    val animationStep:      LiveData<Int> = torchereManager.animationStep

    fun setPrimaryColor(color: Int) {
        torchereManager.writePrimaryColor(color)
    }

    fun setSecondaryColor(color: Int) {
        torchereManager.writeSecondaryColor(color)
    }

    fun setRandomColor(state: Boolean) {
        torchereManager.writeRandomColor(state)
    }

    fun setAnimationMode(mode: Int) {
        torchereManager.writeAnimationMode(mode)
    }

    fun setAnimationMode(mode: FlClassicAnimations) {
        torchereManager.writeAnimationMode(mode.code)
    }

    fun setAnimationOnSpeed(speed: Float) {
        torchereManager.writeAnimationOnSpeed(speed)
    }

    fun setAnimationOffSpeed(speed: Int) {
        torchereManager.writeAnimationOffSpeed(speed)
    }

    fun setAnimationDirection(direction: Int) {
        torchereManager.writeAnimationDirection(direction)
    }

    fun setAnimationDirection(direction: FlClassicAnimationDirections) {
        torchereManager.writeAnimationDirection(direction.code)
    }

    fun setAnimationStep(step: Int) {
        torchereManager.writeAnimationStep(step)
    }
}