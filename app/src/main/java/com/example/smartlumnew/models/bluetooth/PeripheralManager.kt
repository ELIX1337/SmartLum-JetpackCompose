package com.example.smartlumnew.models.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.example.smartlumnew.R
import com.example.smartlumnew.models.data.PeripheralData
import com.example.smartlumnew.models.data.PeripheralError
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.livedata.ObservableBleManager
import no.nordicsemi.android.ble.observer.ConnectionObserver
import java.util.*

enum class ConnectionState(@StringRes val textValue: Int) {
    CONNECTING(R.string.connection_state_connecting),
    CONNECTED(R.string.connection_state_connected),
    FAILED_TO_CONNECT(R.string.connection_state_failed_to_connect),
    READY(R.string.connection_state_ready),
    DISCONNECTING(R.string.connection_state_disconnecting),
    DISCONNECTED(R.string.connection_state_disconnected);
}

open class PeripheralManager(context: Context) : ObservableBleManager(context) {

    companion object {
        val UUID_MASK : UUID = UUID.fromString("BB930000-3CE1-4720-A753-28C0159DC777")
    }

    private var firmwareVersionCharacteristic: BluetoothGattCharacteristic? = null
    private var resetToFactoryCharacteristic:  BluetoothGattCharacteristic? = null
    private var dfuCharacteristic:             BluetoothGattCharacteristic? = null
    private var deviceInitStateCharacteristic: BluetoothGattCharacteristic? = null
    private var deviceErrorCharacteristic:     BluetoothGattCharacteristic? = null

    val peripheralConnectionState = MutableLiveData<ConnectionState>()
    val disconnectReason = MutableLiveData<Int>()
    val isConnected     = MutableLiveData<Boolean>()
    val firmwareVersion = MutableLiveData<Int>()
    val isInitialized   = MutableLiveData<Boolean>()
    val error           = MutableLiveData<PeripheralError>()

    var foundCharacteristics = mutableMapOf<UUID,BluetoothGattCharacteristic>()
    private var supported = false

    override fun shouldClearCacheWhenDisconnected(): Boolean {
        return supported
    }

    private val connectionCallback: ConnectionObserver = object : ConnectionObserver {
        override fun onDeviceConnecting(device: BluetoothDevice) {
            peripheralConnectionState.postValue(ConnectionState.CONNECTING)
            Log.e("TAG", "onDeviceConnecting: ${device.name}" )
        }

        override fun onDeviceConnected(device: BluetoothDevice) {
            peripheralConnectionState.postValue(ConnectionState.CONNECTED)
            Log.e("TAG", "onDeviceConnected: ${device.name}" )
        }

        override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
            peripheralConnectionState.postValue(ConnectionState.FAILED_TO_CONNECT)
            disconnectReason.postValue(reason)
            Log.e("TAG", "onDeviceFailedToConnect: ${device.name}, reason - $reason" )
        }

        override fun onDeviceReady(device: BluetoothDevice) {
            peripheralConnectionState.postValue(ConnectionState.READY)
            Log.e("TAG", "onDeviceReady: ${device.name}" )
        }

        override fun onDeviceDisconnecting(device: BluetoothDevice) {
            peripheralConnectionState.postValue(ConnectionState.DISCONNECTING)
            Log.e("TAG", "onDeviceDisconnecting: ${device.name}" )
        }

        override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
            peripheralConnectionState.postValue(ConnectionState.DISCONNECTED)
            disconnectReason.postValue(reason)
            Log.e("TAG", "onDeviceDisconnected: ${device.name}, reason - $reason")
        }
    }

    init { setConnectionObserver(connectionCallback) }

    protected open inner class PeripheralManagerGattCallback : BleManagerGattCallback() {

        override fun initialize() {
            Log.e("TAG", "initialize: PeripheralManager")
            readCharacteristic(firmwareVersionCharacteristic)
                .with(firmwareVersionCallback)
                .enqueue()
            setNotificationCallback(deviceInitStateCharacteristic)
                .with(deviceInitStateCallBack)
            readCharacteristic(deviceInitStateCharacteristic)
                .with(deviceInitStateCallBack)
                .enqueue()
            enableNotifications(deviceInitStateCharacteristic)
                .enqueue()
            setNotificationCallback(deviceErrorCharacteristic)
                .with(deviceErrorCallback)
            readCharacteristic(deviceErrorCharacteristic)
                .with(deviceErrorCallback)
                .enqueue()
            enableNotifications(deviceErrorCharacteristic)
                .enqueue()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            val deviceInfoService = gatt.getService(DEVICE_INFO_SERVICE_UUID)
            val eventService      = gatt.getService(EVENT_SERVICE_UUID)
            deviceInfoService?.let { initDeviceInfoCharacteristics(it) }
            eventService?.let      { initEventCharacteristics(it) }
            supported = deviceInfoService != null
            return supported
        }

        override fun onDeviceReady() {
            super.onDeviceReady()
            isConnected.postValue(true)
        }

        override fun onServicesInvalidated() {
            Log.e("TAG", "onServicesInvalidated: PeripheralManager")
            isConnected.postValue(false)
            foundCharacteristics.clear()
            firmwareVersionCharacteristic = null
            dfuCharacteristic             = null
            resetToFactoryCharacteristic  = null
            deviceInitStateCharacteristic = null
            deviceErrorCharacteristic     = null
            close()
        }
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return PeripheralManagerGattCallback()
    }

    private fun initDeviceInfoCharacteristics(service: BluetoothGattService) {
        firmwareVersionCharacteristic = service.getCharacteristic(DEVICE_FIRMWARE_VERSION_CHARACTERISTIC_UUID)
        resetToFactoryCharacteristic  = service.getCharacteristic(RESET_TO_FACTORY_CHARACTERISTIC_UUID)
        dfuCharacteristic             = service.getCharacteristic(DEVICE_DFU_CHARACTERISTIC_UUID)
        deviceInitStateCharacteristic = service.getCharacteristic(DEVICE_INIT_STATE_CHARACTERISTIC_UUID)
    }

    private fun initEventCharacteristics(service: BluetoothGattService) {
        deviceErrorCharacteristic = service.getCharacteristic(EVENT_ERROR_CHARACTERISTIC_UUID)
    }

    fun resetToFactorySettings() {
        resetToFactoryCharacteristic?.let {
            writeCharacteristic(
                it,
                PeripheralData.setTrue(),
                WRITE_TYPE_NO_RESPONSE
            )
                .enqueue()
        }
    }

    fun enableDfuMode() {
        dfuCharacteristic?.let {
            writeCharacteristic(
                it,
                PeripheralData.setTrue(),
                WRITE_TYPE_NO_RESPONSE
            )
                .enqueue()
        }
    }

    private val firmwareVersionCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "onFirmwareVersionReceived base manager: $data")
            firmwareVersion.postValue(data)
        }
    }

    private val deviceInitStateCallBack: BooleanDataCallback = object : BooleanDataCallback() {
        override fun onBooleanReceived(device: BluetoothDevice, state: Boolean) {
            Log.e("TAG", "onDevice initialized: $state")
            isInitialized.postValue(state)
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            super.onInvalidDataReceived(device, data)
            Log.e("TAG", "onInvalidDataReceived: Device init" )
        }
    }

    private val deviceErrorCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "onErrorReceived base manager: $data")
            error.postValue(PeripheralError.valueOf(data))
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            Log.e("TAG", "onInvalidDataReceived: ERROR CALLBACK - $data" )
        }
    }

}