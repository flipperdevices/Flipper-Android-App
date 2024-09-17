package com.flipperdevices.keyedit.api

import com.flipperdevices.bridge.dao.api.model.FlipperFile

interface NotSavedFlipperKeyApi {
    suspend fun toNotSavedFlipperFile(flipperFile: FlipperFile): NotSavedFlipperFile
}
