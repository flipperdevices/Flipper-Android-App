package com.flipperdevices.keyparser.impl.parsers.impl

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyparser.impl.parsers.KeyParserDelegate
import java.util.concurrent.TimeUnit
import kotlin.math.abs

private const val KEY_PROTOCOL = "Protocol"
private const val KEY_KEY = "Key"
private const val KEY_RAW = "RAW"
private const val KEY_RAW_DATA = "RAW_Data"

class SubGhzParser : KeyParserDelegate {
    override suspend fun parseKey(
        flipperKey: FlipperKey,
        fff: FlipperFileFormat
    ): FlipperKeyParsed {
        val keyContentAsMap = fff.orderedDict.toMap()

        val totalTimeMs = if (keyContentAsMap[KEY_PROTOCOL].equals(KEY_RAW, ignoreCase = true)) {
            calculateTotalTime(fff)
        } else {
            null
        }

        return FlipperKeyParsed.SubGhz(
            keyName = flipperKey.path.nameWithoutExtension,
            notes = flipperKey.notes,
            protocol = keyContentAsMap[KEY_PROTOCOL],
            key = keyContentAsMap[KEY_KEY],
            totalTimeMs = totalTimeMs
        )
    }

    private fun calculateTotalTime(fff: FlipperFileFormat): Long {
        val totalTimeMcs = fff.orderedDict.filter { it.first == KEY_RAW_DATA }
            .map { pair ->
                pair.second.split(" ").mapNotNull { it.toLongOrNull() }.map { abs(it) }
            }.flatten().sum()

        return TimeUnit.MILLISECONDS.convert(totalTimeMcs, TimeUnit.MICROSECONDS)
    }
}
