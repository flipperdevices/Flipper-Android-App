package com.flipperdevices.bridge.dao.impl.ktx

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.impl.model.FlipperAdditionalFile

internal fun FlipperAdditionalFile.toFlipperFile(): FlipperFile = FlipperFile(
    path = filePath,
    content = content.flipperContent
)
