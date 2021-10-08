package com.example.smartlumnew.utils

import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.smartlumnew.MainActivity

private fun MainActivity.requestPermissions(
    requestedPermissions: Array<String>,
    status: (permission: String, status: Boolean) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
    { permissions ->
        permissions.map {
            status(it.key, it.value)
            if (!it.value) {
                Log.e("TAG", "DENIED PERMISSIONS: ${it.key}")
            } else {
                Log.e("TAG", "GRANTED PERMISSIONS: ${it.key}")
            }
        }
    }.launch(requestedPermissions)
}