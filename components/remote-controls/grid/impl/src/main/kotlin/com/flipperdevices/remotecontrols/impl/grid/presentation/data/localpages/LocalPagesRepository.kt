package com.flipperdevices.remotecontrols.impl.grid.presentation.data.localpages

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.ifrmvp.model.PagesLayout

interface LocalPagesRepository {
    suspend fun getLocalFlipperKey(path: FlipperFilePath): FlipperKey?
    suspend fun getLocalPagesLayout(
        path: FlipperFilePath,
        toPagesLayout: (String) -> PagesLayout?
    ): PagesLayout?

    suspend fun save(
        flipperFilePath: FlipperFilePath,
        remotesRaw: String,
        content: FlipperKeyContent
    )

    suspend fun delete(path: FlipperFilePath)
}
