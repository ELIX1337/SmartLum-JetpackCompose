package com.example.smartlumnew.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.smartlumnew.BuildConfig
import com.example.smartlumnew.R
import com.example.smartlumnew.ui.components.ExpandableCell
import com.example.smartlumnew.ui.components.ValuePickerItem
import com.example.smartlumnew.ui.theme.AppTheme
import com.example.smartlumnew.ui.theme.appTheme
import com.example.smartlumnew.ui.theme.setAppTheme
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kotlinx.coroutines.launch

@Preview
@Composable
fun SettingsPreview() {
    Settings(navController = NavHostController(LocalContext.current))
}

/**
 * Экран настроек приложения
 */
@Composable
fun Settings(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val showDialog = rememberSaveable { mutableStateOf(false) }

    val termsIntent     = Intent(Intent.ACTION_VIEW, Uri.parse("https://smartlum.flycricket.io/terms.html"))
    val licencesIntent  = Intent(context, OssLicensesMenuActivity::class.java)
    val privacyIntent   = Intent(Intent.ACTION_VIEW, Uri.parse("https://smartlum.flycricket.io/privacy.html"))
    val instagramIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/smartlum/"))
    val vkIntent        = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/smartlum"))
    val websiteIntent   = Intent(Intent.ACTION_VIEW, Uri.parse("https://smart-lum.com"))

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Выбор темы
            // Версия приложения
            ExpandableCell(
                headerContent = { Text(stringResource(R.string.settings_general_cell_title)) }
            ) {
                Column {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick =  { showDialog.value = true }
                    ) {
                        Text(stringResource(R.string.settings_app_theme_button))
                    }
                    Surface(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 12.dp)
                    ) {
                        Text(stringResource(R.string.settings_app_version_text) + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")")
                    }
                }
            }

            // About
            ExpandableCell(
                headerContent = { Text(stringResource(R.string.settings_about_cell_title)) }
            ) {
                Column {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { context.startActivity(termsIntent) }
                    ) {
                        Text(stringResource(R.string.settings_terms_button))
                    }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { context.startActivity(licencesIntent) }
                    ) {
                        Text(stringResource(R.string.settings_open_source_licences_button))
                    }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { context.startActivity(privacyIntent) }
                    ) {
                        Text(stringResource(R.string.settings_privacy_button))
                    }
                }
            }

            // Контакты
            ExpandableCell(
                headerContent = { Text(stringResource(R.string.settings_contacts_cell_title)) }
            ) {
                Column {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        icon = painterResource(id = R.drawable.ic_instagram),
                        contentDescription = "Instagram",
                        text = stringResource(R.string.settings_instagram_link_button)
                    ) {
                        context.startActivity(instagramIntent)
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        icon = painterResource(id = R.drawable.ic_vk),
                        contentDescription = "VK",
                        text = stringResource(R.string.settings_vk_link_button)
                    ) {
                        context.startActivity(vkIntent)
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        icon = painterResource(id = R.drawable.ic_web),
                        contentDescription = "Website",
                        text = stringResource(R.string.settings_website_link_button)
                    ) {
                        context.startActivity(websiteIntent)
                    }
                }
            }
        }

        // Диалоговое окно для выбора темы
        ThemeSelectionDialog(
            isOpen = showDialog.value,
            dismiss = { showDialog.value = false },
            selected = appTheme.value,
            onSelected = { scope.launch { setAppTheme(context.applicationContext, it) } })
    }
}

@Composable
fun Button(
    modifier: Modifier = Modifier,
    icon: Painter,
    contentDescription: String,
    text: String = "",
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun ThemeSelectionDialog(
    isOpen: Boolean,
    dismiss: () -> Unit,
    selected: AppTheme,
    onSelected: (AppTheme) -> Unit,
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = dismiss,
            title = {
                Text(stringResource(R.string.alert_choose_app_theme))
            },
            text = {
                Column {
                    ValuePickerItem(
                        title = stringResource(R.string.light_app_theme_radio_button),
                        isSelected = selected == AppTheme.LIGHT_THEME
                    ) {
                        onSelected(AppTheme.LIGHT_THEME)
                    }
                    ValuePickerItem(
                        title = stringResource(R.string.dark_app_theme_radio_button),
                        isSelected = selected == AppTheme.NIGHT_THEME
                    ) {
                        onSelected(AppTheme.NIGHT_THEME)
                    }
                    ValuePickerItem(
                        title = stringResource(R.string.system_app_theme_radion_button),
                        isSelected = selected == AppTheme.SYSTEM_THEME
                    ) {
                        onSelected(AppTheme.SYSTEM_THEME)
                    }
                }
            },
            confirmButton = {
                Button(dismiss) {
                    Text(stringResource(R.string.alert_choose_app_theme_confirm_button))
                }
            }
        )
    }
}
