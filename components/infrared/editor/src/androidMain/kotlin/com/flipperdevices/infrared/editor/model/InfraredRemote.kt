package com.flipperdevices.infrared.editor.model

sealed class InfraredRemote(
    val name: String,
    val type: String
) {
    data class Parsed(
        val nameInternal: String,
        val typeInternal: String,
        val protocol: String,
        val address: String,
        val command: String,
    ) : InfraredRemote(nameInternal, typeInternal)

    data class Raw(
        val nameInternal: String,
        val typeInternal: String,
        val frequency: String,
        val dutyCycle: String,
        val data: String,
    ) : InfraredRemote(nameInternal, typeInternal)

    fun copy(name: String): InfraredRemote {
        return when (this) {
            is Parsed -> copy(nameInternal = name)
            is Raw -> copy(nameInternal = name)
        }
    }
}
