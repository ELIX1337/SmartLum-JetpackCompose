package com.example.smartlumnew.models.viewModels

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import com.example.smartlumnew.models.bluetooth.TorchereManager

class TorchereViewModel(context: Application) : BasePeripheralViewModel(context) {

    private val torchereManager: TorchereManager = TorchereManager(getApplication())
    private var peripheral: BluetoothDevice? = null

    val primaryColor:       LiveData<Int> = torchereManager.primaryColor
    val secondaryColor:     LiveData<Int> = torchereManager.secondaryColor
    val randomColor:        LiveData<Boolean> = torchereManager.randomColor
    val animationMode:      LiveData<Int> = torchereManager.animationMode
    val animationOnSpeed:   LiveData<Int> = torchereManager.animationOnSpeed
    val animationOffSpeed:  LiveData<Int> = torchereManager.animationOffSpeed
    val animationDirection: LiveData<Int> = torchereManager.animationDirection
    val animationStep:      LiveData<Int> = torchereManager.animationStep

}