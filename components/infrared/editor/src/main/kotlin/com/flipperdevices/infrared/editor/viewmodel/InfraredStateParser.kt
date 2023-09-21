package com.flipperdevices.infrared.editor.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.infrared.editor.model.InfraredEditorState
import com.flipperdevices.infrared.editor.model.InfraredRemote

private const val KEY_FILE_TYPE_KEY = "Filetype"
private const val KEY_FILE_TYPE_VALUE = "IR signals file"
private const val KEY_FILE_VERSION_KEY = "Version"
private const val KEY_FILE_VERSION_VALUE = "1"

private const val KEY_NAME = "name"
private const val KEY_TYPE = "type"
private const val KEY_TYPE_RAW = "raw"
private const val KEY_TYPE_PARSED = "parsed"

private const val KEY_PROTOCOL = "protocol"
private const val KEY_ADDRESS = "address"
private const val KEY_COMMAND = "command"

private const val KEY_FREQUENCY = "frequency"
private const val KEY_DUTY_CYCLE = "duty_cycle"
private const val KEY_DATA = "data"
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
