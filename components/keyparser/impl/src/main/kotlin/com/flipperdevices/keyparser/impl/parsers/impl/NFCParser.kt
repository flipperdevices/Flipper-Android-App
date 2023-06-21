package com.flipperdevices.keyparser.impl.parsers.impl

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyparser.impl.parsers.KeyParserDelegate
import java.nio.charset.Charset

private const val KEY_TYPE = "Device type"
private const val KEY_VERSION = "Version"
private const val KEY_UID = "UID"
private const val KEY_ATQA = "ATQA"
private const val KEY_SAK = "SAK"
private const val KEY_MF_CLASSIC_TYPE = "Mifare Classic type"
private const val KEY_MF_VERSION = "Data format version"
private const val KEY_BLOCK = "Block"

class NFCParser : KeyParserDelegate {
    override suspend fun parseKey(
        flipperKey: FlipperKey,
        fff: FlipperFileFormat
    ): FlipperKeyParsed {
        val shadowFile = flipperKey
            .additionalFiles
            .find { it.path.fileType == FlipperFileType.SHADOW_NFC }
        val actualNfcFFF = if (shadowFile != null) {
            val fileContent = shadowFile.content.openStream().use {
                it.readBytes().toString(Charset.defaultCharset())
            }
            FlipperFileFormat.fromFileContent(fileContent)
        } else {
            fff
        }
        val keyContentAsMap = actualNfcFFF.orderedDict.toMap()

        val lines = keyContentAsMap.filter { it.key.startsWith(KEY_BLOCK) }.map {
            it.key.replace(KEY_BLOCK, "").trim().toIntOrNull() to it.value
        }.filterNot { it.first == null }.map { it.first!! to it.second }

        return FlipperKeyParsed.NFC(
            keyName = flipperKey.path.nameWithoutExtension,
            notes = flipperKey.notes,
            deviceType = keyContentAsMap[KEY_TYPE],
            uid = keyContentAsMap[KEY_UID],
            version = keyContentAsMap[KEY_VERSION]?.toIntOrNull() ?: 0,
            atqa = keyContentAsMap[KEY_ATQA],
            sak = keyContentAsMap[KEY_SAK],
            mifareClassicType = keyContentAsMap[KEY_MF_CLASSIC_TYPE],
            dataFormatVersion = keyContentAsMap[KEY_MF_VERSION]?.toIntOrNull() ?: 0,
            lines = lines
        )
    }
}
