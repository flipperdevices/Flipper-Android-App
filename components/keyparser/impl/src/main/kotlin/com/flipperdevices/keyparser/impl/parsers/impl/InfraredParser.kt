package com.flipperdevices.keyparser.impl.parsers.impl

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyparser.impl.parsers.KeyParserDelegate
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

private const val KEY_PROTOCOL = "protocol"

@ContributesMultibinding(AppGraph::class, KeyParserDelegate::class)
class InfraredParser @Inject constructor() : KeyParserDelegate {
    override val flipperType: FlipperKeyType
        get() = FlipperKeyType.INFRARED

    override suspend fun parseKey(
        flipperKey: FlipperKey,
        fff: FlipperFileFormat
    ): FlipperKeyParsed {
        val keyContentAsMap = fff.orderedDict.toMap()

        return FlipperKeyParsed.Infrared(
            keyName = flipperKey.path.nameWithoutExtension,
            notes = flipperKey.notes,
            protocol = keyContentAsMap[KEY_PROTOCOL]
        )
    }
}
