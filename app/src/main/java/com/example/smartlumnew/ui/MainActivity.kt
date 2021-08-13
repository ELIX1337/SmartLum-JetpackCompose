package com.example.smartlumnew.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import com.example.smartlumnew.models.viewModels.ScannerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi

class MainActivity : ComponentActivity() {

    private val scannerViewModel: ScannerViewModel by viewModels()

    @ExperimentalMaterialApi
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartLumApp(scannerViewModel)
        }
    }

    override fun onStop() {
        super.onStop()
        scannerViewModel.stopScan()
    }

}