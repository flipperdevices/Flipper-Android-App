package com.flipperdevices.remotecontrols.impl.grid.presentation.data

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.api.infrared.InfraredBackendApi
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, PagesRepository::class)
class BackendPagesRepository @Inject constructor(
    private val infraredBackendApi: InfraredBackendApi,
) : PagesRepository {

    override suspend fun fetchDefaultPageLayout(
        ifrFileId: Long
    ): Result<PagesLayout> = runCatching<PagesLayout> {
        infraredBackendApi.getUiFile(ifrFileId)
    }

    override suspend fun fetchKeyContent(
        ifrFileId: Long
    ): Result<String> = runCatching {
        infraredBackendApi.getIfrFileContent(ifrFileId).content
    }
}
