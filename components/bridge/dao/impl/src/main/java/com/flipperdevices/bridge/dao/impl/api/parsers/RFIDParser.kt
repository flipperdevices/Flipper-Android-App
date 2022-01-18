package com.flipperdevices.bridge.dao.impl.api.parsers

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import java.nio.charset.Charset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val KEY_TYPE = "Key type"
const val KEY_DATA = "Data"

class RFIDParser : KeyParserDelegate {
    override suspend fun parseKey(
        flipperKey: FlipperKey
    ): FlipperKeyParsed = withContext(Dispatchers.IO) {
        val keyContentAsPairs = flipperKey.keyContent.stream().use {
            it.readBytes().toString(Charset.defaultCharset())
        }.split("\n")
            .filterNot { it.startsWith("#") }
            .map { it.substringBefore(":").trim() to it.substringAfter(":").trim() }
        val keyContentAsMap = keyContentAsPairs.toMap()

        return@withContext FlipperKeyParsed.RFID(
            keyName = flipperKey.path.name,
            data = keyContentAsMap[KEY_DATA],
            keyType = keyContentAsMap[KEY_TYPE]
        )
    }
}
