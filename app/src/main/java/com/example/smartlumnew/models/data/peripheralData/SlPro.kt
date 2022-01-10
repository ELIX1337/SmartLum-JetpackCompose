package com.example.smartlumnew.models.data.peripheralData

import com.example.smartlumnew.R
import com.example.smartlumnew.models.data.AnimationSettings
import com.example.smartlumnew.models.data.PeripheralDataElement

enum class SlProControllerType: PeripheralDataElement {
    Default {
        override val code: Int
            get() = 0
        override val elementNameStringID: Int
            get() = R.string.sl_pro_controller_type_default
    },
    RGB {
        override val code: Int
            get() = 1
        override val elementNameStringID: Int
            get() = R.string.sl_pro_controller_type_rgb
    };

    companion object {
        fun valueOf(code: Int): SlProControllerType? = values().find { it.code == code }
    }
}

enum class SlProStairsWorkModes: PeripheralDataElement {
    ByTimer {
        override val code: Int
            get() = 0
        override val elementNameStringID: Int
            get() = R.string.sl_pro_stairs_work_mode_by_timer
    },
    BySensors {
        override val code: Int
            get() = 1
        override val elementNameStringID: Int
            get() = R.string.sl_pro_stairs_work_mode_by_sensors
    };

    companion object {
        fun valueOf(code: Int): SlProStairsWorkModes? = values().find { it.code == code }
    }
}

enum class SlProAdaptiveModes(val supportingSettings: List<SlProAdaptiveSettings>):
    PeripheralDataElement {
    Off(
        listOf(
            SlProAdaptiveSettings.LedBrightness,
            SlProAdaptiveSettings.StandbyLightingBrightness
        )
    ) {
        override val code: Int
            get() = 0
        override val elementNameStringID: Int
            get() = R.string.sl_pro_adaptive_mode_off
    },

    ByTopSensors(
        listOf()
    ) {
        override val code: Int
            get() = 1
        override val elementNameStringID: Int
            get() = R.string.sl_pro_adaptive_mode_by_top_sensors
    },
    ByBotSensors(
        listOf()
    ) {
        override val code: Int
            get() = 2
        override val elementNameStringID: Int
            get() = R.string.sl_pro_adaptive_mode_by_bottom_sensors
    },
    Average(
        listOf()
    ) {
        override val code: Int
            get() = 3
        override val elementNameStringID: Int
            get() = R.string.sl_pro_adaptive_mode_average
    };

    companion object {
        fun valueOf(code: Int): SlProAdaptiveModes? = values().find { it.code == code }
    }
}

enum class SlProAnimations(val supportingSettings: List<AnimationSettings>):
    PeripheralDataElement {
    Tetris(
        listOf(
            AnimationSettings.PrimaryColor,
            AnimationSettings.SecondaryColor,
            AnimationSettings.RandomColor,
            AnimationSettings.Direction,
            AnimationSettings.Speed)
    ) {
        override val code: Int
            get() = 1
        override val elementNameStringID: Int
            get() = R.string.peripheral_animation_mode_name_tetris
    },

    Wave(
        listOf(
            AnimationSettings.PrimaryColor,
            AnimationSettings.SecondaryColor,
            AnimationSettings.Direction,
            AnimationSettings.Speed,
            AnimationSettings.Step)
    ) {
        override val code: Int
            get() = 2
        override val elementNameStringID: Int
            get() = R.string.peripheral_animation_mode_name_wave
    };

    companion object {
        fun valueOf(code: Int): SlProAnimations? = SlProAnimations.values().find { it.code == code }
    }
}

enum class SlProAdaptiveSettings {
    LedBrightness,
    StandbyLightingBrightness
}