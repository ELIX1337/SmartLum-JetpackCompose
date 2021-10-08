package com.example.smartlumnew.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.smartlumnew.BuildConfig
import com.example.smartlumnew.R
import com.example.smartlumnew.ui.components.ValuePickerItem
import com.example.smartlumnew.ui.theme.AppTheme
import com.example.smartlumnew.ui.theme.appTheme
import com.example.smartlumnew.ui.theme.contrastTransparent
import com.example.smartlumnew.ui.theme.setAppTheme
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kotlinx.coroutines.launch

@Preview
@Composable
fun SettingsPreview() {
    Settings(navController = NavHostController(LocalContext.current))
}

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val showDialog = rememberSaveable { mutableStateOf(false) }

    val termsIntent = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://smartlum.flycricket.io/terms.html")
        ) }
    val privacyIntent = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://smartlum.flycricket.io/privacy.html")
        ) }
    val vkIntent = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://smartlum.flycricket.io/privacy.html")
        ) }
    val instagramIntent = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.instagram.com/smartlum/")
        ) }
    val websiteIntent = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://smart-lum.com")
        ) }
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
            ExpandableCell(
                headerContent = { Text("About") }
            ) {
                Column {
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            context.startActivity(termsIntent)
                        }
                    ) {
                        Text("Terms of use")
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            context.startActivity(
                                Intent(
                                    context,
                                    OssLicensesMenuActivity::class.java
                                )
                            )
                        }
                    ) {
                        Text("Licences")
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            context.startActivity(privacyIntent)
                        }
                    ) {
                        Text("Privacy policy")
                    }
                }
            }

            ExpandableCell(
                headerContent = { Text("General") }
            ) {
                Column {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick =  { showDialog.value = true }
                    ) {
                        Text("Theme")
                    }
                    Surface(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 12.dp)) {
                        Text("Version - ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                    }
                }
            }
            ExpandableCell(
                headerContent = { Text("Contacts") }
            ) {
                Column {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { context.startActivity(instagramIntent) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_instagram),
                            contentDescription = "Instagram",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Instagram")
                    }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { context.startActivity(websiteIntent) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_web),
                            contentDescription = "Website",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Website")
                    }
                }
            }
        }
        ThemeSelectionDialog(
            isOpen = showDialog.value,
            dismiss = { showDialog.value = false },
            selected = appTheme.value,
            onSelected = { scope.launch { setAppTheme(context.applicationContext, it) } })
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
                Text("Choose theme")
            },
            text = {
                Column {
                    ValuePickerItem(
                        title = "Light",
                        isSelected = selected == AppTheme.LIGHT_THEME
                    ) {
                        onSelected(AppTheme.LIGHT_THEME)
                    }
                    ValuePickerItem(
                        title = "Dark",
                        isSelected = selected == AppTheme.NIGHT_THEME
                    ) {
                        onSelected(AppTheme.NIGHT_THEME)
                    }
                    ValuePickerItem(
                        title = "System",
                        isSelected = selected == AppTheme.SYSTEM_THEME
                    ) {
                        onSelected(AppTheme.SYSTEM_THEME)
                    }
                }
            },
            confirmButton = {
                Button(dismiss) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ExpandableCell(
    modifier: Modifier = Modifier,
    headerContent: @Composable (() -> Unit),
    bodyContent: @Composable (() -> Unit)? = null,
) {
    val styledContent: @Composable (() -> Unit)? = bodyContent?.let {
        @Composable {
            val style = MaterialTheme.typography.body2.copy(
                textAlign = TextAlign.Center,
            )
            ProvideTextStyle(style, content = bodyContent)
        }
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = modifier
            .animateContentSize(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
            .border(BorderStroke(1.dp, contrastTransparent()))
            .fillMaxWidth()
            .defaultMinSize(minHeight = 44.dp),
        elevation = 1.dp
    ) {
        Column {
            Surface(
                Modifier
                    .clickable { expanded = !expanded }
                    .fillMaxWidth()
                    .padding(16.dp, 12.dp)) {
                Row {
                    Box(Modifier.weight(9f)) {
                        headerContent()
                    }
                    Icon(
                        imageVector = if (!expanded) Icons.Rounded.ExpandMore else Icons.Rounded.ExpandLess,
                        contentDescription = "Expand icon")
                }
            }
            if (expanded) {
                styledContent?.let {
                    Surface {
                        Divider()
                        Box(
                            Modifier
                                .padding(16.dp, 12.dp)) {
                            it()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsTopBar(
    onBackPressed: () -> Unit
) {
    TopAppBar {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { onBackPressed() }) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                "About",
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6)
        }
    }
}
