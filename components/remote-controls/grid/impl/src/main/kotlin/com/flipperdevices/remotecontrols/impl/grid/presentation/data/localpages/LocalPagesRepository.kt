package com.flipperdevices.remotecontrols.impl.grid.presentation.data.localpages

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.ifrmvp.model.PagesLayout

interface LocalPagesRepository {
    suspend fun getLocalFlipperKey(ifrFileId: Long): FlipperKey?
    suspend fun getLocalPagesLayout(ifrFileId: Long): PagesLayout?

    suspend fun save(
        ifrFileId: Long,
        remotesRaw: String,
        pagesLayout: PagesLayout
    )
    suspend fun delete(ifrFileId: Long)
}
