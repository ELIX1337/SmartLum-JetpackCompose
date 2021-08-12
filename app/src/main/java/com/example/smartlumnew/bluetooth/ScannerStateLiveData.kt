package com.example.smartlumnew.bluetooth

import androidx.lifecycle.LiveData

class ScannerStateLiveData (bluetoothEnabled: Boolean, locationEnabled: Boolean) : LiveData<ScannerStateLiveData>() {

    var isScanning: Boolean
        private set
    private var isBluetoothEnabled: Boolean
    private var locationEnabled: Boolean
    private var hasRecords: Boolean = false

    /* package */
    init {
        this.isScanning = false
        this.isBluetoothEnabled = bluetoothEnabled
        this.locationEnabled    = locationEnabled
        postValue(this)
    }

    fun refresh() {
        postValue(this)
    }

    fun scanningStarted() {
        isScanning = true
        postValue(this)
    }

    fun scanningStopped() {
        isScanning = false
        postValue(this)
    }

    fun bluetoothEnabled() {
        isBluetoothEnabled = true
        postValue(this)
    }

    @Synchronized
    fun bluetoothDisabled() {
        isBluetoothEnabled = false
        hasRecords = false
        postValue(this)
    }

    fun isBluetoothEnabled() : Boolean {
        return isBluetoothEnabled
    }

    fun setLocationEnabled(state: Boolean) {
        this.locationEnabled = state
        postValue(this)
    }

    fun isLocationEnabled(): Boolean {
        return locationEnabled
    }

    fun recordFound() {
        hasRecords = true
        postValue(this)
    }

    fun hasRecords(): Boolean {
        return hasRecords
    }

    fun clearRecords() {
        hasRecords = false
        postValue(this)
    }
}