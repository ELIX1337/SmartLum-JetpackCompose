package com.example.smartlumnew

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.smartlumnew.ui.theme.appTheme
import com.example.smartlumnew.ui.theme.getSavedAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * IDE говорит что этот класс нигде не используется? Не слушай ее (чекай Манифест)
 * Этот класс используется чтобы выставить необходимую тему приложения.
 *
 * Этого не найдешь нигде в интернете:
 * Везде, где речь идет о теме оформления в Jetpack Compose,
 * тупо меняют оформление в MainActivity (SmartLumTheme), но почему-то никто не говорит о том,
 * что это всего лишь смена переменных цвета, шрифтов и тд.
 * Но по факту приложение работает в дефолтном оформлении, просто с другими цветами.
 * Проблема, в том, что в приожении могут быть использованы разные res файлы.
 * Например какое-нибудь лого в темном или светлом оформлении, и в зависимости от темы приложения,
 * нужно грузить соответствующий drawable.
 * Соответсвенно нужно поменять оформление на уровне темы приложения.
 * Здесь это и происходит в методе setDefaultNightMode().
 * Ты можешь удалить этот метод и заметишь, что выбранная пользователем в настройках тема нормально прогрузится,
 * но открой меню навигации слева и увидишь в чем проблема (темный Drawable в светлой теме и наоборот).
 *
 * Если это засунуть в MainActivity,
 * то при каждом запуске будет происходить ее перезагрузка при смене темы, выглядит как лаг.
 */
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        appTheme.value = runBlocking(Dispatchers.IO) {
            getSavedAppTheme(baseContext)
        }
        AppCompatDelegate.setDefaultNightMode(appTheme.value.defaultNightMode)
    }
}