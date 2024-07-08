package com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal.mapping

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.ifrmvp.backend.model.SignalModel

private const val KEY_FILE_TYPE_KEY = "Filetype"
private const val KEY_FILE_TYPE_VALUE = "IR signals file"
private const val KEY_FILE_VERSION_KEY = "Version"
private const val KEY_FILE_VERSION_VALUE = "1"

private const val KEY_NAME = "name"
private const val KEY_TYPE = "type"

private const val KEY_PROTOCOL = "protocol"
private const val KEY_ADDRESS = "address"
private const val KEY_COMMAND = "command"

private const val KEY_FREQUENCY = "frequency"
private const val KEY_DUTY_CYCLE = "duty_cycle"
private const val KEY_DATA = "data"

internal fun SignalModel.toFFFormat(): FlipperFileFormat {
    return FlipperFileFormat(
        orderedDict = listOf(
            (KEY_FILE_TYPE_KEY to KEY_FILE_TYPE_VALUE),
            (KEY_FILE_VERSION_KEY to KEY_FILE_VERSION_VALUE),
            (KEY_NAME to name),
            (KEY_TYPE to type),
            (KEY_FREQUENCY to frequency),
            (KEY_DUTY_CYCLE to dutyCycle),
            (KEY_DATA to data),
            (KEY_PROTOCOL to protocol),
            (KEY_ADDRESS to address),
            (KEY_COMMAND to command),
        ).mapNotNull { (k, v) -> if (v == null) null else k to v }
    )
}