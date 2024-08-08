package com.flipperdevices.remotecontrols.impl.grid.remote.presentation.data.pages

import com.flipperdevices.ifrmvp.model.PagesLayout

interface PagesRepository {
    suspend fun fetchDefaultPageLayout(ifrFileId: Long): Result<PagesLayout>
    suspend fun fetchKeyContent(ifrFileId: Long): Result<String>
}
