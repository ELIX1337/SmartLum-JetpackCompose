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

/**
 * Стандартный менеджер Bluetooth.
 * От него наследуются контретные менеджеры для конкретных устройств.
 * Здесь происходит обработка стандартных для всех устройств Smartlum данных.
 * Можно посмотреть реализацию у Nordic (приложение NRF Blinky или Android-BLE-Library) на гитхабе
 */
open class PeripheralManager(context: Context) : ObservableBleManager(context) {

    /**
     * Маска, по которой будет происходить фильтрация найденных устройств
     * Как можно заметить, все рекламные UUID отличаются лишь 4 байтами (нули),
     * остальное все одинаковое, это позволяет использовать маску при сканировании,
     * а не массив UUID.
     */
    companion object {
        val UUID_MASK : UUID = UUID.fromString("BB930000-3CE1-4720-A753-28C0159DC777")
    }

    // Дефолтные характеристики, которые есть на во всех устройствах (почти)
    var firmwareVersionCharacteristic: BluetoothGattCharacteristic? = null
    var serialNumberCharacteristic:    BluetoothGattCharacteristic? = null
    var resetToFactoryCharacteristic:  BluetoothGattCharacteristic? = null
    var dfuCharacteristic:             BluetoothGattCharacteristic? = null
    var deviceInitStateCharacteristic: BluetoothGattCharacteristic? = null
    var deviceErrorCharacteristic:     BluetoothGattCharacteristic? = null
    var demoModeStateCharacteristic:   BluetoothGattCharacteristic? = null

    // Дефолтные параметры
    val peripheralConnectionState = MutableLiveData<ConnectionState>()
    val disconnectReason = MutableLiveData<Int>()
    val isConnected     = MutableLiveData<Boolean>()
    val firmwareVersion = MutableLiveData<Int>()
    val serialNumber    = MutableLiveData<String>()
    val isInitialized   = MutableLiveData<Boolean>()
    val error           = MutableLiveData<PeripheralError>()
    val demoMode        = MutableLiveData<Boolean>()

    // Этот массив на самом деле не используетя
    // Я его ввел для тестирования кое каких решений
    // Аналогия с endpoints в приложении на iOS
    var foundCharacteristics = mutableMapOf<UUID,BluetoothGattCharacteristic>()

    // Если false - то будет дисконнект
    // Работает так, мы ищем конкретные сервисы
    // Если что-то не нашли, то переменная становится false
    // (метод isRequiredServiceSupported)
    // можно принудительно сделать true, ничего не сломается
    private var supported = false

    // По идее роли не играет вообще
    override fun shouldClearCacheWhenDisconnected(): Boolean {
        return supported
    }

    /**
     * Создаем объект для обработки событий подключения и тд.
     */
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

    /**
     * Отдаем вышесозданный объект observer'у
     */
    init { setConnectionObserver(connectionCallback) }

    /**
     * Дефолтный колбэк, стандартный для всех устройств.
     * Переопределяем его в классах-наследниках (не забываем вызвать super метод)
     * для конкретный устройств
     */
    protected open inner class PeripheralManagerGattCallback : BleManagerGattCallback() {

        override fun initialize() {
            Log.e("TAG", "initialize: PeripheralManager")
            readCharacteristic(firmwareVersionCharacteristic)
                .with(firmwareVersionCallback)
                .enqueue()
            readCharacteristic(serialNumberCharacteristic)
                .with(serialNumberCallback)
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
            val deviceInfoService = gatt.getService(LEGACY_DEVICE_INFO_SERVICE_UUID)
            val eventService      = gatt.getService(LEGACY_EVENT_SERVICE_UUID)
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

    /**
     * Говорим, каким колбэком обрабатывать BLE
     */
    override fun getGattCallback(): BleManagerGattCallback {
        return PeripheralManagerGattCallback()
    }

    open fun initDeviceInfoCharacteristics(service: BluetoothGattService) {
        firmwareVersionCharacteristic = service.getCharacteristic(LEGACY_DEVICE_FIRMWARE_VERSION_CHARACTERISTIC_UUID)
        serialNumberCharacteristic    = service.getCharacteristic(LEGACY_DEVICE_SERIAL_NUMBER_CHARACTERISTIC_UUID)
        resetToFactoryCharacteristic  = service.getCharacteristic(LEGACY_RESET_TO_FACTORY_CHARACTERISTIC_UUID)
        dfuCharacteristic             = service.getCharacteristic(LEGACY_DEVICE_DFU_CHARACTERISTIC_UUID)
        deviceInitStateCharacteristic = service.getCharacteristic(LEGACY_DEVICE_INIT_STATE_CHARACTERISTIC_UUID)
        demoModeStateCharacteristic   = service.getCharacteristic(DEVICE_DEMO_MODE_STATE_CHARACTERISTIC_UUID)
    }

    open fun initEventCharacteristics(service: BluetoothGattService) {
        deviceErrorCharacteristic = service.getCharacteristic(LEGACY_EVENT_ERROR_CHARACTERISTIC_UUID)
    }

    // Дальше идут публичные методы
    // Они дефолтные для всех устройств

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

    fun writeDemoMode(state: Boolean) {
        demoModeStateCharacteristic?.let {
            writeCharacteristic(
                it,
                if (state) PeripheralData.setTrue() else PeripheralData.setFalse(),
                WRITE_TYPE_NO_RESPONSE
            ).enqueue()
            demoMode.postValue(state)
        }
    }

    // Здесь мы пишем, как будем обрабатывать считывание данных с конкретной характеристики.
    // Для этого создаем колбэк, и назначаем его характеристике в методе initialize()

    private val firmwareVersionCallback: SingleByteDataCallback = object : SingleByteDataCallback() {
        override fun onIntegerValueReceived(device: BluetoothDevice, data: Int) {
            Log.e("TAG", "onFirmwareVersionReceived base manager: $data")
            firmwareVersion.postValue(data)
        }
    }

    private val serialNumberCallback: StringDataCallback = object : StringDataCallback() {
        override fun onStringReceived(device: BluetoothDevice, string: String) {
            Log.e("TAG", "onSerialNumberReceiver base manager: $string")
            serialNumber.postValue(string)
        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            Log.e("TAG", "onInvalidDataReceived: Serial number" )
        }
    }

    private val deviceInitStateCallBack: BooleanDataCallback = object : BooleanDataCallback() {
        override fun onBooleanReceived(device: BluetoothDevice, state: Boolean) {
            Log.e("TAG", "onDeviceinitialized: $state")
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