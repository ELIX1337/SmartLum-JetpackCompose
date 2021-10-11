package com.example.smartlumnew.models.viewModels

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartlumnew.models.bluetooth.ConnectionState
import com.example.smartlumnew.models.bluetooth.DiscoveredPeripheral
import com.example.smartlumnew.models.bluetooth.PeripheralManager
import com.example.smartlumnew.models.bluetooth.PeripheralProfileEnum

class PeripheralViewModelFactory(private val context: Application, private val type: PeripheralProfileEnum?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when (type) {
            PeripheralProfileEnum.SL_BASE -> {
                if (modelClass.isAssignableFrom(SLBaseViewModel::class.java)) {
                    return SLBaseViewModel(context) as T
                }
            }
            PeripheralProfileEnum.FL_MINI,
            PeripheralProfileEnum.FL_CLASSIC -> {
                if (modelClass.isAssignableFrom(FLClassicViewModel::class.java)) {
                    return FLClassicViewModel(context) as T
                }
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class - $type")
    }
}

open class PeripheralViewModel(manager: PeripheralManager) : ViewModel() {

    val peripheralManager: PeripheralManager = manager
    private var peripheral: BluetoothDevice? = null

    val firmwareVersion: LiveData<Int>             = manager.firmwareVersion
    val isInitialized:   LiveData<Boolean>         = manager.isInitialized
    val isConnected:     LiveData<Boolean>         = manager.isConnected
    val connectionState: LiveData<ConnectionState> = manager.peripheralConnectionState
    val disconnectReason:LiveData<Int>            = manager.disconnectReason

    val _hasOptions = MutableLiveData(false)
    val hasOptions: LiveData<Boolean> = _hasOptions

    fun connect(target: DiscoveredPeripheral) {
        if (peripheral == null) {
            peripheral = target.device
        }
        reconnect()
    }

    fun connect(target: BluetoothDevice) {
        if (peripheral == null) {
            peripheral = target
            reconnect()
        }
    }

    private fun reconnect() {
        if (peripheral != null) {
            peripheralManager.connect(peripheral!!)
                .retry(3, 100)
                .useAutoConnect(false)
                .enqueue()
        }
    }

    fun disconnect() {
        peripheral = null
        peripheralManager.disconnect().enqueue()
    }

    fun resetToFactorySettings() {
        peripheralManager.resetToFactorySettings()
    }

    fun enableDfuMode() {
        peripheralManager.enableDfuMode()
    }

    open fun commit() { }

    override fun onCleared() {
        super.onCleared()
        Log.e("TAG", "onCleared: ON CLEARED" )
        disconnect()
        peripheralManager.close()
    }

}