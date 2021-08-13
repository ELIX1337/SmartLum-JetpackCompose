package com.example.smartlumnew.models.viewModels

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.location.LocationManager
import android.os.ParcelUuid
import android.util.Log
import androidx.lifecycle.*
import com.example.smartlumnew.models.bluetooth.PeripheralsLiveData
import com.example.smartlumnew.models.bluetooth.TorchereManager
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import no.nordicsemi.android.support.v18.scanner.*
import com.example.smartlumnew.utils.Utils
import java.lang.IllegalArgumentException

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    val scanResult = PeripheralsLiveData()

    private val _isScanning = MutableLiveData(false)
    val isScanning: LiveData<Boolean> = _isScanning

    private val _isBluetoothEnabled = MutableLiveData(Utils.isBleEnabled(application))
    val isBluetoothEnabled: LiveData<Boolean> = _isBluetoothEnabled

    private val _isLocationEnabled = MutableLiveData(Utils.isLocationEnabled(application))
    val isLocationEnabled: LiveData<Boolean> = _isLocationEnabled

    private val _isLocationGranted = MutableLiveData(Utils.isPermissionGranted(application, Manifest.permission.ACCESS_FINE_LOCATION))
    val isLocationGranted: LiveData<Boolean> = _isLocationGranted
    fun setLocationPermissionStatus(isGranted: Boolean) {
        _isLocationGranted.postValue(isGranted)
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().unregisterReceiver(bluetoothStateBroadcastReceiver)
        getApplication<Application>().unregisterReceiver(locationStateBroadcastReceiver)
    }

    fun refresh() {
        if (isScanning.value == true) {
            return
        }
    }

    fun startScan() {
        if (_isScanning.value!!) {
            Log.e("TAG", "startScan: scanner is already scanning")
            return
        }
        Log.e("TAG", "startScan - value = ${_isScanning.value}")
        val uuidList: MutableList<ParcelUuid> = ArrayList()
        uuidList.add(ParcelUuid(TorchereManager.TORCHERE_SERVICE_UUID))
        val filters: MutableList<ScanFilter> = ArrayList()
        for (a in uuidList) {
            filters.add(ScanFilter.Builder().setServiceUuid(a).build())
        }
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(500)
            .setUseHardwareBatchingIfSupported(false)
            .build()
        val scanner = BluetoothLeScannerCompat.getScanner()
        try {
            scanner.startScan(null, scanSettings, scanCallback)
            _isScanning.postValue(true)
        } catch (error: IllegalArgumentException) {
            Log.e("TAG", "startScan: scanner already scanning - $error" )
        }
    }

    fun stopScan() {
        if (isScanning.value!! && isBluetoothEnabled.value!!) {
            Log.e("TAG", "stopScan")
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.stopScan(scanCallback)
            _isScanning.postValue(false)
        }
    }

    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (scanResult.peripheralDiscovered(result)) {
                scanResult.applyFilter()
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            Log.e("TAG", "onBatchScanResults: $results")
            var atLeastOneMatchedFilter = false
            results.forEach { result ->
                atLeastOneMatchedFilter = scanResult.peripheralDiscovered(result) || atLeastOneMatchedFilter
            }
            if (atLeastOneMatchedFilter) {
                scanResult.applyFilter()
            }
        }

        override fun onScanFailed(errorCode: Int) {
            _isScanning.postValue(false)
            Log.e("TAG", "onScanFailed - $errorCode" )
        }
    }

    private fun registerBroadcastReceivers(application: Application) {
        application.registerReceiver(
            bluetoothStateBroadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED).apply {
                addAction(BluetoothAdapter.EXTRA_STATE)
                addAction(BluetoothAdapter.EXTRA_PREVIOUS_STATE) }
        )
        application.registerReceiver(
            locationStateBroadcastReceiver,
            IntentFilter(LocationManager.MODE_CHANGED_ACTION)
        )
    }

    private val bluetoothStateBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
            val previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF)
            when (state) {
                BluetoothAdapter.STATE_ON -> {
                    _isBluetoothEnabled.postValue(true)
                }
                BluetoothAdapter.STATE_TURNING_OFF, BluetoothAdapter.STATE_OFF ->
                    if (previousState != BluetoothAdapter.STATE_TURNING_OFF && previousState != BluetoothAdapter.STATE_OFF) {
                        stopScan()
                        _isBluetoothEnabled.postValue(false)
                        scanResult.clear()
                    }
            }
        }
    }

    private val locationStateBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val enabled = Utils.isLocationEnabled(context)
            _isLocationEnabled.postValue(enabled)
            Log.e("TAG", "onReceive: location status ${!isLocationEnabled.value!!}")
        }
    }

    init {
        registerBroadcastReceivers(application)
    }

}