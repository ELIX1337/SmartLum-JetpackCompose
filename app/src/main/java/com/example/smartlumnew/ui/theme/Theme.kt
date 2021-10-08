package com.example.smartlumnew.ui.theme

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val DarkColorPalette = darkColors(
    primary = SlYellow,
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
//    secondaryVariant = Color(0xFF03DAC6),
//    background = Color(0xFF121212),
//    surface = Color(0xFF121212),
    error = Color(0xFFCF6679),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(
    name = "app_settings"
)

object PREFERENCES_KEYS {
    val APP_THEME = intPreferencesKey(name = "app_theme")
}

suspend fun saveAppTheme(context: Context, theme: AppTheme){
    context.dataStore.edit { preferences ->
        preferences[PREFERENCES_KEYS.APP_THEME] = theme.code
    }
}

suspend fun getSavedAppTheme(context: Context): AppTheme {
    val theme = context.dataStore.data
        .map { preferences ->
            preferences[PREFERENCES_KEYS.APP_THEME] ?: 0
        }
    return AppTheme.valueOf(theme.first()) ?: AppTheme.SYSTEM_THEME
}

enum class AppTheme(val code: Int, val defaultNightMode: Int) {
    SYSTEM_THEME(0, MODE_NIGHT_FOLLOW_SYSTEM),
    NIGHT_THEME(1, MODE_NIGHT_YES),
    LIGHT_THEME(2, MODE_NIGHT_NO);

    companion object {
        fun valueOf(code: Int): AppTheme? =
            values().find { it.code == code }
    }
}

val appTheme = mutableStateOf(AppTheme.SYSTEM_THEME)

suspend fun setAppTheme(context: Context, theme: AppTheme) {
    saveAppTheme(context, theme)
    appTheme.value = theme
    when (appTheme.value) {
        AppTheme.SYSTEM_THEME -> setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        AppTheme.NIGHT_THEME  -> setDefaultNightMode(MODE_NIGHT_YES)
        AppTheme.LIGHT_THEME  -> setDefaultNightMode(MODE_NIGHT_NO)
    }
}

@Composable
fun isAppInDarkTheme(): Boolean {
    return when (appTheme.value) {
        AppTheme.SYSTEM_THEME -> isSystemInDarkTheme()
        AppTheme.NIGHT_THEME -> true
        AppTheme.LIGHT_THEME -> false
    }
}

@Composable
fun SmartLumTheme(
    darkTheme: Boolean =
        when (appTheme.value) {
            AppTheme.SYSTEM_THEME -> isSystemInDarkTheme()
            AppTheme.NIGHT_THEME -> true
            AppTheme.LIGHT_THEME -> false
        },
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val systemBarsColor = themeTransparent()
    SideEffect {
        if (darkTheme) {
            systemUiController.setSystemBarsColor(
                color = systemBarsColor
            )
        } else {
            systemUiController.setSystemBarsColor(
                color = systemBarsColor,
            )
        }
    }

    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )

}