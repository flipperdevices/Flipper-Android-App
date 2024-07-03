package com.flipperdevices.remotecontrols.impl.grid.presentation.data

import com.flipperdevices.ifrmvp.api.backend.ApiBackend
import com.flipperdevices.ifrmvp.model.PagesLayout

internal class BackendPagesRepository(
    private val apiBackend: ApiBackend
) : PagesRepository {

    override suspend fun fetchDefaultPageLayout(
        ifrFileId: Long
    ): Result<PagesLayout> = kotlin.runCatching {
        val pagesLayout = KitchenLayoutFactory.create()
        pagesLayout
    }

    override suspend fun fetchKeyContent(
        ifrFileId: Long
    ): Result<String> = kotlin.runCatching {
        ""
    }
}
