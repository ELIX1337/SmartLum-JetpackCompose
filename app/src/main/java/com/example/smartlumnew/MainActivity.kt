package com.example.smartlumnew

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.smartlumnew.models.viewModels.ScannerViewModel

class MainActivity : AppCompatActivity() {

    // ViewModel Bluetooth сканнера
    // Инициализируем тут, так как нужен контекст активити
    private val scannerViewModel: ScannerViewModel by viewModels()

    /**
     * Точка входа в приложение
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Показываем splashScreen
        installSplashScreen()

        // Растягиваем приложение по всему экрану (убираем отступы от системных баров)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Приложение
        setContent {
            SmartLumApp(scannerViewModel)
        }
    }

    override fun onStop() {
        super.onStop()
        scannerViewModel.stopScan()
    }

}