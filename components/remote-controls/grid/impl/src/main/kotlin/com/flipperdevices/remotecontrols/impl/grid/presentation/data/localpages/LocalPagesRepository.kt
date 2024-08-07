package com.flipperdevices.remotecontrols.impl.grid.presentation.data.localpages

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.ifrmvp.model.PagesLayout

interface LocalPagesRepository {
    suspend fun getLocalFlipperKey(path: FlipperFilePath): FlipperKey?
    suspend fun getLocalPagesLayout(
        path: FlipperFilePath,
        toPagesLayout: (String) -> PagesLayout?
    ): PagesLayout?
}
