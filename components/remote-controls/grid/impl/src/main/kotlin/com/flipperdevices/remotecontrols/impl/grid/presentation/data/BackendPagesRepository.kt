package com.flipperdevices.remotecontrols.impl.grid.presentation.data

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.api.backend.ApiBackend
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.serialization.json.Json
import javax.inject.Inject

@ContributesBinding(AppGraph::class, PagesRepository::class)
class BackendPagesRepository @Inject constructor(
    private val apiBackend: ApiBackend
) : PagesRepository {

    override suspend fun fetchDefaultPageLayout(
        ifrFileId: Long
    ): Result<PagesLayout> = kotlin.runCatching<PagesLayout> {
//        val pagesLayout = KitchenLayoutFactory.create()
//        val s = Json {
//            prettyPrint = true
//        }.encodeToString(pagesLayout)
//        Log.d("MAKEEVRSERG", s)
        val pagesLayout = apiBackend.getUiFile(ifrFileId)
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            isLenient = true
        }.decodeFromString(pagesLayout)
    }.onFailure(Throwable::printStackTrace)

    override suspend fun fetchKeyContent(
        ifrFileId: Long
    ): Result<String> = kotlin.runCatching {
        apiBackend.getIfrFileContent(ifrFileId).content
    }
}
