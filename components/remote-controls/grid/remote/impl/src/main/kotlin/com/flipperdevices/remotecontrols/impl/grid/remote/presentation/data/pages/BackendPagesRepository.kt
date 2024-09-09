package com.flipperdevices.remotecontrols.impl.grid.remote.presentation.data.pages

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.api.infrared.FlipperInfraredBackendApi
import com.flipperdevices.ifrmvp.backend.model.toPagesLayout
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, PagesRepository::class)
class BackendPagesRepository @Inject constructor(
    private val infraredBackendApi: FlipperInfraredBackendApi,
) : PagesRepository {
    override suspend fun fetchDefaultPageLayout(
        ifrFileId: Long
    ): Result<PagesLayout> = runCatching<PagesLayout> {
        infraredBackendApi.getUiFile(ifrFileId).toPagesLayout()
    }

    override suspend fun fetchKeyContent(
        ifrFileId: Long
    ): Result<String> = runCatching {
        infraredBackendApi.getIfrFileContent(ifrFileId).content
    }
}
