package com.flipperdevices.remotecontrols.impl.setup.util

import com.flipperdevices.infrared.editor.core.model.InfraredRemote

internal fun InfraredRemote.toByteArray(): ByteArray {
    val bytesList = when (this) {
        is InfraredRemote.Parsed -> listOf(
            type,
            protocol,
            address,
            command
        )

        is InfraredRemote.Raw -> listOf(
            type,
            frequency,
            dutyCycle,
            data
        )
    }
    return bytesList
        .map(String::toByteArray)
        .flatMap(ByteArray::asList)
        .toByteArray()
}
