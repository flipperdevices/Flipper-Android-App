package com.flipperdevices.infrared.editor.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.infrared.InfraredControl
import kotlinx.collections.immutable.ImmutableList

private const val KEY_NAME = "name"
private const val KEY_TYPE = "type"

private const val KEY_TYPE_PARSED = "parsed"
private const val KEY_PROTOCOL = "protocol"
private const val KEY_ADDRESS = "address"
private const val KEY_COMMAND = "command"

private const val KEY_TYPE_RAW = "raw"
private const val KEY_FREQUENCY = "frequency"
private const val KEY_DUTY_CYCLE = "duty_cycle"
private const val KEY_DATA = "data"

private val allFields = listOf(
    KEY_NAME,
    KEY_TYPE,
    KEY_PROTOCOL,
    KEY_ADDRESS,
    KEY_COMMAND,
    KEY_FREQUENCY,
    KEY_DUTY_CYCLE,
    KEY_DATA
)

object InfraredEditorSaver {
    fun newFlipperKey(
        oldKey: FlipperKey,
        remotes: ImmutableList<InfraredControl>
    ): FlipperKey {
        val newContent = newContent(oldKey, remotes)

        val newFlipperFile = FlipperFile(
            path = oldKey.mainFile.path,
            content = newContent
        )

        return FlipperKey(
            mainFile = newFlipperFile,
            additionalFiles = listOf(),
            notes = oldKey.notes,
            synchronized = false,
            deleted = false
        )
    }

    private fun newContent(
        oldKey: FlipperKey,
        remotes: ImmutableList<InfraredControl>
    ): FlipperKeyContent {
        val fff = FlipperFileFormat.fromFlipperContent(oldKey.keyContent)
        val infraredControl = generateInfraredContent(remotes)

        val header = fff.orderedDict.filter { allFields.contains(it.first).not() }

        return FlipperFileFormat(header + infraredControl)
    }

    private fun generateInfraredContent(
        remotes: ImmutableList<InfraredControl>
    ): List<Pair<String, String>> {
        val content = mutableListOf<Pair<String, String>>()

        remotes.forEach { remote ->
            when (remote) {
                is InfraredControl.Parsed -> {
                    content.add(KEY_NAME to remote.name)
                    content.add(KEY_TYPE to KEY_TYPE_PARSED)
                    content.add(KEY_PROTOCOL to remote.protocol)
                    content.add(KEY_ADDRESS to remote.address)
                    content.add(KEY_COMMAND to remote.command)
                }
                is InfraredControl.Raw -> {
                    content.add(KEY_NAME to remote.name)
                    content.add(KEY_TYPE to KEY_TYPE_RAW)
                    content.add(KEY_FREQUENCY to remote.frequency)
                    content.add(KEY_DUTY_CYCLE to remote.dutyCycle)
                    content.add(KEY_DATA to remote.data)
                }
            }
        }

        return content
    }
}
