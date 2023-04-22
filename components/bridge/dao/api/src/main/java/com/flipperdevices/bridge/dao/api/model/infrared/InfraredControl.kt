package com.flipperdevices.bridge.dao.api.model.infrared

sealed class InfraredControl(val name: String) {
    data class Parsed(
        val nameInternal: String,
        val protocol: String,
        val address: String,
        val command: String,
    ) : InfraredControl(nameInternal)

    data class Raw(
        val nameInternal: String,
        val frequency: String,
        val dutyCycle: String,
        val data: String,
    ) : InfraredControl(nameInternal)
}
