package com.flipperdevices.remotecontrols.impl.grid.presentation.data

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.api.backend.ApiBackend
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, PagesRepository::class)
class BackendPagesRepository @Inject constructor(
    private val apiBackend: ApiBackend,
) : PagesRepository {

    override suspend fun fetchDefaultPageLayout(
        ifrFileId: Long
    ): Result<PagesLayout> = runCatching<PagesLayout> {
        apiBackend.getUiFile(ifrFileId)
    }

    override suspend fun fetchKeyContent(
        ifrFileId: Long
    ): Result<String> = runCatching {
        apiBackend.getIfrFileContent(ifrFileId).content
    }
}
