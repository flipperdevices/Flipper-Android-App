package com.flipperdevices.faphub.dao.network.api

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.faphub.dao.api.FapVersionApi
import com.flipperdevices.faphub.dao.network.ktorfit.api.KtorfitVersionApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

private const val MAX_QUERY_ARRAY_SIZE = 10

@ContributesBinding(AppGraph::class, FapVersionApi::class)
class FapVersionApiImpl @Inject constructor(
    private val ktorfitVersionApi: KtorfitVersionApi
) : FapVersionApi, LogTagProvider {
    override val TAG = "FapVersionApi"

    override suspend fun getVersionsMap(versions: List<String>): Map<String, SemVer> {
        val fetchedVersions = versions.chunked(MAX_QUERY_ARRAY_SIZE)
            .map { ktorfitVersionApi.getVersions(it) }
            .flatten()

        return fetchedVersions.associate {
            val numberVersion = SemVer.fromString(it.version)
                ?: error("Failed parse ${it.version}")
            it.id to numberVersion
        }
    }
}
