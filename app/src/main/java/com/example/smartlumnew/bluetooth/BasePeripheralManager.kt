package com.example.smartlumnew.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.smartlumnew.bluetooth.callbacks.FirmwareVersionDataCallback
import no.nordicsemi.android.ble.livedata.ObservableBleManager

open class BasePeripheralManager(context: Context) : ObservableBleManager(context) {

    private var firmwareVersionCharacteristic : BluetoothGattCharacteristic? = null
    private var dfuCharacteristic : BluetoothGattCharacteristic? = null

    val firmwareVersion = MutableLiveData<Int>()

    protected open inner class BasePeripheralManagerGattCallback : BleManagerGattCallback() {
        override fun initialize() {
            Log.e("TAG", "initialize: BASE")
            readCharacteristic(firmwareVersionCharacteristic).with(firmwareVersionCallback).enqueue()
            enableNotifications(dfuCharacteristic).enqueue()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            Log.e("TAG", "isRequiredServiceSupported: BASE PERIPHERAL")
            val deviceInfoService = gatt.getService(DEVICE_INFO_SERVICE_UUID)
            deviceInfoService?.let { initDeviceInfoCharacteristics(it) }
            for (service in gatt.services) {
                for (characteristic in service.characteristics) {
                    Log.e("TAG", "isRequiredServiceSupported: ${characteristic.uuid}" )
                    readCharacteristic(characteristic)
                }
            }
            Log.e("TAG", "isRequiredServiceSupported: ${gatt.requestMtu(512)}")
            return deviceInfoService != null
        }

        override fun onDeviceDisconnected() {
            firmwareVersionCharacteristic = null
            dfuCharacteristic             = null
        }
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return BasePeripheralManagerGattCallback()
    }

    private fun initDeviceInfoCharacteristics(service: BluetoothGattService) {
        firmwareVersionCharacteristic = service.getCharacteristic(DEVICE_FIRMWARE_VERSION_CHARACTERISTIC_UUID)
        dfuCharacteristic = service.getCharacteristic(DEVICE_DFU_CHARACTERISTIC_UUID)
    }

    private val firmwareVersionCallback: FirmwareVersionDataCallback = object : FirmwareVersionDataCallback() {
            override fun onFirmwareVersionReceived(device: BluetoothDevice, version: Int) {
                Log.d("TAG", "onFirmwareVersionReceived: $version")
                firmwareVersion.postValue(version)
            }
        }

}