package com.flipperdevices.remotecontrols.impl.grid.presentation.data

import com.flipperdevices.ifrmvp.model.PagesLayout

internal interface PagesRepository {
    suspend fun fetchDefaultPageLayout(ifrFileId: Long): Result<PagesLayout>
    suspend fun fetchKeyContent(ifrFileId: Long): Result<String>
}
