package com.flipperdevices.remotecontrols.impl.grid.remote.presentation.data.pages

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.api.infrared.FlipperInfraredBackendApi
import com.flipperdevices.ifrmvp.backend.model.toPagesLayout
import com.flipperdevices.ifrmvp.model.PagesLayout
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<PagesRepository>())
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
