package com.example.smartlumnew

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.smartlumnew.ui.theme.appTheme
import com.example.smartlumnew.ui.theme.getSavedAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        appTheme.value = runBlocking(Dispatchers.IO) {
            getSavedAppTheme(baseContext)
        }
        AppCompatDelegate.setDefaultNightMode(appTheme.value.defaultNightMode)
    }
}