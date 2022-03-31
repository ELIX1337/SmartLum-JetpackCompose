package com.example.smartlumnew.utils.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.smartlumnew.R
import com.example.smartlumnew.ui.components.BottomSheetAlert
import com.google.accompanist.permissions.*

// Получение расрешений
// Код взят и подогнан из документации
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
    var cancelClicked by rememberSaveable { mutableStateOf(false) }

    multiplePermissionsState.permissions.forEach { permissionState ->

        when (permissionState.status) {

            PermissionStatus.Granted -> onPermissionsGranted(true)

            is PermissionStatus.Denied -> {

                if (permissionState.status.shouldShowRationale || cancelClicked) {
                    if (doNotShowRationaleClicked != null) {
                        doNotShowRationaleClicked()
                    }
                    BottomSheetAlert(
                        titleText = stringResource(R.string.alert_permissions_denied_title),
                        descriptionText = declinedText,
                        positiveButtonText = stringResource(R.string.alert_permissions_denied_navigate_to_settings_button),
                        onPositiveButtonClick = navigateToSettingsScreen
                    )
                } else {
                    BottomSheetAlert(
                        titleText = stringResource(R.string.alert_permissions_required_title),
                        descriptionText = requestText,
                        positiveButtonText = stringResource(R.string.alert_permissions_required_request_button),
                        negativeButtonText = stringResource(R.string.alert_permissions_required_do_not_show_rationale_button),
                        onPositiveButtonClick = { multiplePermissionsState.launchMultiplePermissionRequest() },
                        onNegativeButtonClick = { cancelClicked = true }
                    )
                }
            }

        }
    }

}