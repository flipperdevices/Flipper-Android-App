package com.flipperdevices.bridge.dao.impl.ktx

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.bridge.dao.impl.model.FlipperAdditionalFile

internal fun FlipperAdditionalFile.toFlipperFile(): FlipperFile = FlipperFile(
    path = filePath,
    content = content.flipperContent
)

internal fun FlipperFile.toDatabaseFile(keyId: Int) = FlipperAdditionalFile(
    path = path.pathToKey,
    content = DatabaseKeyContent(content),
    keyId = keyId
)
