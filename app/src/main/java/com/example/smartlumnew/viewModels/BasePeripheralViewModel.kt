package com.example.smartlumnew.viewModels

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.smartlumnew.bluetooth.BasePeripheralManager
import com.example.smartlumnew.bluetooth.DiscoveredBluetoothDevice

open class BasePeripheralViewModel(context: Application) : AndroidViewModel(context) {

    private val basePeripheralManager: BasePeripheralManager = BasePeripheralManager(context)
    private var peripheral: BluetoothDevice? = null

    val firmwareVersion: LiveData<Int> = basePeripheralManager.firmwareVersion

    fun connect(target: DiscoveredBluetoothDevice) {
        if (peripheral == null) {
            peripheral = target.device
            reconnect()
        }
    }

    fun connect(target: BluetoothDevice) {
        if (peripheral == null) {
            Log.e("TAG", "connect")
            peripheral = target
            reconnect()
        }
    }

    fun reconnect() {
        if (peripheral != null) {
            basePeripheralManager.connect(peripheral!!)
                .retry(3, 100)
                .useAutoConnect(false)
                .enqueue()
        }
    }

    fun disconnect() {
        peripheral = null
        basePeripheralManager.disconnect().enqueue()
    }
}