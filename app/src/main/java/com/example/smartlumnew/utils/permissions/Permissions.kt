package com.example.smartlumnew.utils.permissions

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import com.example.smartlumnew.ui.components.BottomSheetAlert
import com.google.accompanist.permissions.*

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
                        titleText = "Permissions denied",
                        descriptionText = declinedText,
                        positiveButtonText = "Go to settings",
                        onPositiveButtonClick =  navigateToSettingsScreen
                    )
                }
            } else {
                BottomSheetAlert(
                    titleText = "Permissions required",
                    descriptionText = requestText,
                    positiveButtonText = "Request permissions",
                    negativeButtonText = "Do not show rationale again",
                    onPositiveButtonClick = { multiplePermissionsState.launchMultiplePermissionRequest() },
                    onNegativeButtonClick = { doNotShowRationale = true }
                )
            }
        }
        // User denied some permission and we can't request it anymore
        // Sending to the Settings screen
        else -> {
            BottomSheetAlert(
                titleText = "Permissions denied",
                descriptionText = declinedText,
                positiveButtonText = "Go to settings",
                onPositiveButtonClick =  navigateToSettingsScreen
            )
        }
    }
}