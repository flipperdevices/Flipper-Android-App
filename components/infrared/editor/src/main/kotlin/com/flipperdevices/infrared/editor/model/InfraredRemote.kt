package com.flipperdevices.infrared.editor.model

sealed class InfraredRemote(val name: String) {
    data class Parsed(
        val nameInternal: String,
        val protocol: String,
        val address: String,
        val command: String,
    ) : InfraredRemote(nameInternal)

    data class Raw(
        val nameInternal: String,
        val frequency: String,
        val dutyCycle: String,
        val data: String,
    ) : InfraredRemote(nameInternal)
}
