package com.flipperdevices.bridge.dao.impl.api.parsers

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val KEY_TYPE = "Key type"
const val KEY_DATA = "Data"

class RFIDParser : KeyParserDelegate {
    override suspend fun parseKey(
        flipperKey: FlipperKey
    ): FlipperKeyParsed = withContext(Dispatchers.IO) {
        val keyContentAsPairs = flipperKey.keyContent.stream().use {
            it.readBytes().toString()
        }.split("\n").map { it.split(":") }.map { it[0].trim() to it[1].trim() }
        val keyContentAsMap = keyContentAsPairs.toMap()

        return@withContext FlipperKeyParsed.RFID(
            keyName = flipperKey.path.name,
            data = keyContentAsMap[KEY_DATA],
            keyType = keyContentAsMap[KEY_TYPE]
        )
    }
}
