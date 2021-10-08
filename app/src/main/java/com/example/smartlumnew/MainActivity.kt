package com.example.smartlumnew

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.smartlumnew.models.viewModels.ScannerViewModel

class MainActivity : AppCompatActivity() {

    private val scannerViewModel: ScannerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SmartLumApp(scannerViewModel)
        }
    }

    override fun onStop() {
        super.onStop()
        scannerViewModel.stopScan()
    }

}