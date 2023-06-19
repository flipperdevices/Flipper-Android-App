package com.flipperdevices.keyparser.impl.parsers.impl

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyparser.impl.parsers.KeyParserDelegate
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, KeyParserDelegate::class)
class UnrecognizedParser @Inject constructor() : KeyParserDelegate {
    override val flipperType: FlipperKeyType?
        get() = null

    override suspend fun parseKey(
        flipperKey: FlipperKey,
        fff: FlipperFileFormat
    ): FlipperKeyParsed {
        return FlipperKeyParsed.Unrecognized(
            flipperKey.path.nameWithoutExtension,
            flipperKey.notes,
            flipperKey.path.keyType,
            fff.orderedDict.toImmutableList()
        )
    }
}
