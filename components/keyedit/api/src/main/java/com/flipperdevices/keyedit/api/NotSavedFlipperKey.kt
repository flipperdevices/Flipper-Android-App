package com.flipperdevices.keyedit.api

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import kotlinx.serialization.Serializable

@Serializable
data class NotSavedFlipperKey(
    val mainFile: NotSavedFlipperFile,
    val additionalFiles: List<NotSavedFlipperFile>,
    val notes: String?
)

@Serializable
data class NotSavedFlipperFile(
    val path: FlipperFilePath,
    val content: FlipperKeyContent
)
