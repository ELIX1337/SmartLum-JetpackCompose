package com.example.smartlumnew.ui.components

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartlumnew.R
import com.example.smartlumnew.utils.permissions.RequestPermissions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@Composable
fun BluetoothEnableRequestSheet(isBluetoothEnabled: Boolean) {
    val context = LocalContext.current
    if (!isBluetoothEnabled) {
        BottomSheetAlert(
            titleText = stringResource(R.string.alert_bluetooth_disabled_title),
            descriptionText = stringResource(R.string.alert_bluetooth_disabled_description),
            positiveButtonText = stringResource(R.string.alert_bluetooth_disabled_enable),
            onPositiveButtonClick = {
                val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                manager.adapter.enable()
            }
        )
    }
}

@Composable
fun LocationEnableRequestSheet(isLocationEnabled: Boolean) {
    val context = LocalContext.current
    if (!isLocationEnabled) {
        BottomSheetAlert(
            titleText = stringResource(R.string.alert_location_disabled_title),
            descriptionText = stringResource(R.string.alert_location_disabled_description),
            positiveButtonText = stringResource(R.string.alert_location_disabled_enable),
            onPositiveButtonClick = {
                context.startActivity(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK))
            }
        )
    }
}

@ExperimentalPermissionsApi
@Composable
fun PermissionRequestSheet(
    requestText: String,
    deniedText: String,
    permissionStatus: Boolean,
    permissions: MultiplePermissionsState,
    onPermissionGranted: (Boolean) -> Unit,
    doNotShowRationaleClicked: @Composable (() -> Unit)? = null) {
    val context = LocalContext.current
    if (!permissionStatus) {
        RequestPermissions(
            requestText,
            deniedText,
            permissions,
            onPermissionsGranted = onPermissionGranted,
            doNotShowRationaleClicked = doNotShowRationaleClicked,
            navigateToSettingsScreen = {
                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        )
    }
}

@Composable
fun BottomSheetAlert(
    titleText: String,
    descriptionText: String,
    positiveButtonText: String,
    negativeButtonText: String? = "Cancel",
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: (() -> Unit)? = null
) {
    Column {
        Text(text = titleText,
            style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        Text(descriptionText)
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onPositiveButtonClick) {
            Text(positiveButtonText)
        }
        if (onNegativeButtonClick != null) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNegativeButtonClick) {
                Text(negativeButtonText!!)
            }
        }
    }
}
