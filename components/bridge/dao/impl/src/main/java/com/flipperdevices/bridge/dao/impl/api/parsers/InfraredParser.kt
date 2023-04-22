package com.flipperdevices.bridge.dao.impl.api.parsers

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.infrared.InfraredControl
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

private const val KEY_NAME = "name"

private const val KEY_PROTOCOL = "protocol"
private const val KEY_ADDRESS = "address"
private const val KEY_COMMAND = "command"

private const val KEY_FREQUENCY = "frequency"
private const val KEY_DUTY_CYCLE = "duty_cycle"
private const val KEY_DATA = "data"

private const val REMOTE_FIELD_COUNT = 5

class InfraredParser : KeyParserDelegate {
    override suspend fun parseKey(
        flipperKey: FlipperKey,
        fff: FlipperFileFormat
    ): FlipperKeyParsed {
        val keyContentAsMap = fff.orderedDict.toMap()
        val keyProtocol = keyContentAsMap[KEY_PROTOCOL]

        return FlipperKeyParsed.Infrared(
            keyName = flipperKey.path.nameWithoutExtension,
            notes = flipperKey.notes,
            protocol = keyProtocol,
            remotes = parseRemote(keyContent = fff.orderedDict)
        )
    }

    private fun parseRemote(keyContent: List<Pair<String, String>>): List<InfraredControl> {
        val keyRemotes = keyContent.drop(2).toMutableList() // Remove Filetype and Version
        val remotes = mutableListOf<InfraredControl>()

        if (keyRemotes.size % REMOTE_FIELD_COUNT != 0) {
            return remotes
        }

        while (keyRemotes.isNotEmpty()) {
            val tryRemote = keyRemotes.take(REMOTE_FIELD_COUNT)
            val tryRemoteMap = tryRemote.toMap()
            val lastField = tryRemote.last()

            when (lastField.first) {
                KEY_COMMAND -> {
                    remotes.add(getParseRemoteControl(tryRemoteMap))
                }
                KEY_DATA -> {
                    remotes.add(getRawRemoteControl(tryRemoteMap))
                }
                else -> {}
            }

            repeat(REMOTE_FIELD_COUNT) { keyRemotes.removeAt(0) }
        }

        return remotes
    }

    private fun getParseRemoteControl(fields: Map<String, String>): InfraredControl.Parsed {
        return InfraredControl.Parsed(
            nameInternal = fields[KEY_NAME] ?: "",
            protocol = fields[KEY_PROTOCOL] ?: "",
            address = fields[KEY_ADDRESS] ?: "",
            command = fields[KEY_COMMAND] ?: "",
        )
    }

    private fun getRawRemoteControl(fields: Map<String, String>): InfraredControl.Raw {
        return InfraredControl.Raw(
            nameInternal = fields[KEY_NAME] ?: "",
            frequency = fields[KEY_FREQUENCY] ?: "",
            dutyCycle = fields[KEY_DUTY_CYCLE] ?: "",
            data = fields[KEY_DATA] ?: "",
        )
    }
}
