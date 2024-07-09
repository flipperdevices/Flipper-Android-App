package com.flipperdevices.infrared.editor.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
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
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_TYPE_PARSED
import com.flipperdevices.infrared.editor.core.util.InfraredFileFormat.KEY_TYPE_RAW
import com.flipperdevices.infrared.editor.model.InfraredEditorState

object InfraredStateParser {
    fun mapStateToFlipperKey(
        oldKey: FlipperKey,
        state: InfraredEditorState.Ready
    ): FlipperKey {
        val newContent = newContent(state)
        val newFlipperFile = FlipperFile(
            path = oldKey.mainFile.path,
            content = newContent
        )

        return FlipperKey(
            notes = oldKey.notes,
            synchronized = false,
            deleted = false,
            mainFile = newFlipperFile
        )
    }

    private fun newContent(state: InfraredEditorState.Ready): FlipperKeyContent {
        val result = mutableListOf<Pair<String, String>>()

        result.apply {
            add(KEY_FILE_TYPE_KEY to KEY_FILE_TYPE_VALUE)
            add(KEY_FILE_VERSION_KEY to KEY_FILE_VERSION_VALUE)
        }

        state.remotes.forEach {
            when (it) {
                is InfraredRemote.Raw -> {
                    result.add(KEY_NAME to it.nameInternal)
                    result.add(KEY_TYPE to KEY_TYPE_RAW)
                    result.add(KEY_FREQUENCY to it.frequency)
                    result.add(KEY_DUTY_CYCLE to it.dutyCycle)
                    result.add(KEY_DATA to it.data)
                }
                is InfraredRemote.Parsed -> {
                    result.add(KEY_NAME to it.nameInternal)
                    result.add(KEY_TYPE to KEY_TYPE_PARSED)
                    result.add(KEY_PROTOCOL to it.protocol)
                    result.add(KEY_ADDRESS to it.address)
                    result.add(KEY_COMMAND to it.command)
                }
            }
        }
        return FlipperFileFormat(result)
    }
}
