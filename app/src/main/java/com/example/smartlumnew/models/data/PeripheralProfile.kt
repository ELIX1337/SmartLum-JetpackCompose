package com.example.smartlumnew.models.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.smartlumnew.R
import java.util.*

/**
 *  Static data
 */
enum class PeripheralProfileEnum(
    @StringRes val type: Int,
    val UUID: UUID,
    @StringRes val peripheralName: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int
) {
    UNKNOWN(
        type = R.string.peripheral_type_unknown,
        UUID = UUID.fromString("12345678-0000-0000-0000-000000000000"),
        peripheralName = R.string.peripheral_name_unknown,
        description = R.string.peripheral_description_unknown,
        image = R.drawable.googleg_standard_color_18
    ),
    FL_CLASSIC(
        type = R.string.peripheral_type_torchere,
        UUID = UUID.fromString("BB930001-3CE1-4720-A753-28C0159DC777"),
        peripheralName = R.string.peripheral_name_fl_classic,
        description = R.string.peripheral_description_fl_classic,
        image = R.drawable.image_torchere
    ),
    FL_MINI(
        type = R.string.peripheral_type_table_lamp,
        UUID = UUID.fromString("BB930002-3CE1-4720-A753-28C0159DC777"),
        peripheralName = R.string.peripheral_name_fl_mini,
        description = R.string.peripheral_description_fl_mini,
        image = R.drawable.image_torchere_mini
    ),
    SL_BASE(
        type = R.string.peripheral_type_stairs_lighting,
        UUID = UUID.fromString("BB930003-3CE1-4720-A753-28C0159DC777"),
        peripheralName = R.string.peripheral_name_sl_base,
        description = R.string.peripheral_description_sl_base,
        image = R.drawable.image_stairs
    ),   
    SL_PRO(
        type = R.string.peripheral_type_stairs_lighting,
        UUID = UUID.fromString("BB930004-3CE1-4720-A753-28C0159DC777"),
        peripheralName = R.string.peripheral_name_sl_pro,
        description = R.string.peripheral_description_sl_pro,
        image = R.drawable.image_stairs_rgb
    ),
    SL_STANDART(
        type = R.string.peripheral_type_stairs_lighting,
        UUID = UUID.fromString("BB930005-3CE1-4720-A753-28C0159DC777"),
        peripheralName = R.string.peripheral_name_sl_standart,
        description = R.string.peripheral_description_sl_pro,
        image = R.drawable.image_stairs
    );

    companion object {
        fun getType(uuid: List<UUID>): PeripheralProfileEnum? {
            return values().firstOrNull { peripheralType ->
                uuid.contains(peripheralType.UUID)
            }
        }


    }
}