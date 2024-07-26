package com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal.mapping

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.ifrmvp.backend.model.SignalModel
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_ADDRESS
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_COMMAND
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_DATA
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_DUTY_CYCLE
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_FILE_TYPE_KEY
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_FILE_TYPE_VALUE
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_FILE_VERSION_KEY
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_FILE_VERSION_VALUE
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_FREQUENCY
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_NAME
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_PROTOCOL
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_TYPE

internal fun SignalModel.toFFFormat(): FlipperFileFormat {
    return FlipperFileFormat(
        orderedDict = listOf(
            (KEY_FILE_TYPE_KEY to KEY_FILE_TYPE_VALUE),
            (KEY_FILE_VERSION_KEY to KEY_FILE_VERSION_VALUE),
            (KEY_NAME to remote.name),
            (KEY_TYPE to remote.type),
            (KEY_FREQUENCY to remote.frequency),
            (KEY_DUTY_CYCLE to remote.dutyCycle),
            (KEY_DATA to remote.data),
            (KEY_PROTOCOL to remote.protocol),
            (KEY_ADDRESS to remote.address),
            (KEY_COMMAND to remote.command),
        ).mapNotNull { (k, v) -> if (v == null) null else k to v }
    )
}
