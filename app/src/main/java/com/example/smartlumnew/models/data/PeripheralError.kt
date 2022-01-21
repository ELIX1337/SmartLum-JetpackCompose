package com.example.smartlumnew.models.data

import androidx.annotation.StringRes
import com.example.smartlumnew.R

/**
 * Здесь перечислены ошибки, которые могут возникнуть на устройстве, и их коды.
 * Когда на устройстве происходит ошибка, то приходит ее соответствующий код,
 * по этому коду определяется тип ошибки
 */
enum class PeripheralError(val code: Int, @StringRes val description: Int) {

    SENS_TOP(0x01, R.string.peripheral_error_code_1),
    SENS_BOTTOM(0x02, R.string.peripheral_error_code_2);

    companion object {
        fun valueOf(code: Int): PeripheralError? = values().find { it.code == code }
    }
}