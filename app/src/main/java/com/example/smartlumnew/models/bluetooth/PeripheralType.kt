package com.example.smartlumnew.models.bluetooth

import java.util.*

enum class PeripheralType(
    val uuid: UUID
) {
    FL_CLASSIC(UUID.fromString("BB930001-3CE1-4720-A753-28C0159DC777")),
    FL_MINI(UUID.fromString("BB930002-3CE1-4720-A753-28C0159DC777"));

    companion object {
        fun getType(uuid: List<UUID>): PeripheralType? {
            return values().firstOrNull { peripheralType ->
                uuid.contains(peripheralType.uuid)
            }
        }
    }
}