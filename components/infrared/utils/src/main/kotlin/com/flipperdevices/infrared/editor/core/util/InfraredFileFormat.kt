package com.flipperdevices.infrared.editor.core.util

object InfraredFileFormat {
    const val KEY_FILE_TYPE_KEY = "Filetype"
    const val KEY_FILE_TYPE_VALUE = "IR signals file"
    const val KEY_FILE_VERSION_KEY = "Version"
    const val KEY_FILE_VERSION_VALUE = "1"

    const val KEY_NAME = "name"
    const val KEY_TYPE = "type"
    const val KEY_TYPE_RAW = "raw"
    const val KEY_TYPE_PARSED = "parsed"

    const val KEY_PROTOCOL = "protocol"
    const val KEY_ADDRESS = "address"
    const val KEY_COMMAND = "command"

    const val KEY_FREQUENCY = "frequency"
    const val KEY_DUTY_CYCLE = "duty_cycle"
    const val KEY_DATA = "data"

    val allFields = listOf(
        KEY_NAME,
        KEY_TYPE,
        KEY_PROTOCOL,
        KEY_ADDRESS,
        KEY_COMMAND,
        KEY_FREQUENCY,
        KEY_DUTY_CYCLE,
        KEY_DATA
    )
}
