package com.example.smartlumnew.utils.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.smartlumnew.R
import com.example.smartlumnew.ui.components.BottomSheetAlert
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@ExperimentalPermissionsApi
@Composable
fun RequestPermissions(
    requestText: String,
    declinedText: String,
    multiplePermissionsState: MultiplePermissionsState,
    onPermissionsGranted: (Boolean) -> Unit,
    doNotShowRationaleClicked: @Composable (() -> Unit)?,
    navigateToSettingsScreen: () -> Unit
) {
    // Track if the user doesn't want to see the rationale any more.
    var doNotShowRationale by rememberSaveable { mutableStateOf(false) }

    when {
        // All permissions are granted
        multiplePermissionsState.allPermissionsGranted -> onPermissionsGranted(true)
        // User sees the permissions for the first time
        // Or user denied any permission but a rationale should be shown
        multiplePermissionsState.shouldShowRationale || !multiplePermissionsState.permissionRequested -> {
            // Showing custom rationale screen (may be empty)
            if (doNotShowRationale) {
                if (doNotShowRationaleClicked != null) {
                    doNotShowRationaleClicked()
                }
                // Showing default rationale screen if custom not implemented
                else {
                    BottomSheetAlert(
                        titleText = stringResource(R.string.alert_permissions_denied_title),
                        descriptionText = declinedText,
                        positiveButtonText = stringResource(R.string.alert_permissions_denied_navigate_to_settings_button),
                        onPositiveButtonClick =  navigateToSettingsScreen
                    )
                }
            } else {
                BottomSheetAlert(
                    titleText = stringResource(R.string.alert_permissions_required_title),
                    descriptionText = requestText,
                    positiveButtonText = stringResource(R.string.alert_permissions_required_request_button),
                    negativeButtonText = stringResource(R.string.alert_permissions_required_do_not_show_rationale_button),
                    onPositiveButtonClick = { multiplePermissionsState.launchMultiplePermissionRequest() },
                    onNegativeButtonClick = { doNotShowRationale = true }
                )
            }
        }
        // User denied some permission and we can't request it anymore
        // Sending to the Settings screen
        else -> {
            BottomSheetAlert(
                titleText = stringResource(R.string.alert_permissions_denied_title),
                descriptionText = declinedText,
                positiveButtonText = stringResource(R.string.alert_permissions_denied_navigate_to_settings_button),
                onPositiveButtonClick =  navigateToSettingsScreen
            )
        }
    }
}