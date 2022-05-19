@file:Suppress("NonExhaustiveWhenStatementMigration")

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
import com.example.smartlumnew.models.data.PeripheralProfileEnum
import com.example.smartlumnew.models.data.PeripheralError

/** Фабрика для получения соответствующих ViewModel.
 * По какой-то причине выдает Exception если приложение какое-то время
 * находится в свернутом режиме.
 * (Полагаю теряется destinationPeripheral в AppNavigation, но это не точно)
 */
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
            PeripheralProfileEnum.SL_STANDART,
            PeripheralProfileEnum.SL_PRO -> {
                if (modelClass.isAssignableFrom(SLProStandartViewModel::class.java)) {
                    return SLProStandartViewModel(context) as T
                }
            }
            else -> {
                Log.e("TAG", "PeripheralViewModelFactory: UNKNOWN TYPE - $type ")
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class - $type")
    }
}

/**
 *  Базовая ViewModel для устройств.
 *  Содержит стандартные методы для всех девайсов.
 *  От него наследуются конкретные ViewModel и расширяют ее фукционал.
 *  Основная ее проблема в том, что она использует Context, а точнее, его используется PeripheralManager.
 *  Поэтому процесс ее инициализации довольно неудобный и может вызвать утечки памяти (но я вроде пофиксил).
 */
open class PeripheralViewModel(manager: PeripheralManager) : ViewModel() {

    val peripheralManager: PeripheralManager = manager
    private var peripheral: BluetoothDevice? = null

    // Инициализируется после коннекта
    var peripheralType: PeripheralProfileEnum? = PeripheralProfileEnum.UNKNOWN

    val firmwareVersion:  LiveData<Int>             = manager.firmwareVersion
    val serialNumber:     LiveData<String>             = manager.serialNumber
    val isInitialized:    LiveData<Boolean>         = manager.isInitialized
    val isConnected:      LiveData<Boolean>         = manager.isConnected
    val connectionState:  LiveData<ConnectionState> = manager.peripheralConnectionState
    val disconnectReason: LiveData<Int>             = manager.disconnectReason
    val error:            LiveData<PeripheralError> = manager.error
    val demoMode:         LiveData<Boolean>         = manager.demoMode

    // Переменная, которая говорит, имеет ли устройство расширенные настройки.
    val _hasOptions = MutableLiveData(false)
    val hasOptions: LiveData<Boolean> = _hasOptions

    fun connect(target: DiscoveredPeripheral) {
        if (peripheral == null) {
            peripheral = target.device
            peripheralType = target.type
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

    fun setDemoMode(state: Boolean) {
        peripheralManager.writeDemoMode(state)
    }

    // Этот метод отправляет данные для первичной настройки (инициализации) устройства.
    // т.е. по нажатию кнопки "Подтвердить", срабатывает этот метод,
    // который отправит на устройство все выставленные настройки.
    // Значения этих настроек хранятся в переменных у классов-наследников
    open fun commit(): Boolean {
        return false
    }

    // Сообщает, готовы ли данные для первичной настройки.
    // Реализуется в наследниках
    open fun isInitDataReady(): Boolean {
        return false
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
        peripheralManager.close()
    }

}