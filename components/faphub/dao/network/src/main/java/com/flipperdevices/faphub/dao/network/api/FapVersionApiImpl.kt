package com.flipperdevices.faphub.dao.network.api

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.faphub.dao.api.FapVersionApi
import com.flipperdevices.faphub.dao.api.model.FapItemVersion
import com.flipperdevices.faphub.dao.network.retrofit.api.KtorfitVersionApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapVersionApi::class)
class FapVersionApiImpl @Inject constructor(
    private val ktorfitVersionApi: KtorfitVersionApi
) : FapVersionApi, LogTagProvider {
    override val TAG = "FapVersionApi"

    override suspend fun getVersions(versions: List<String>): List<FapItemVersion> {
        return ktorfitVersionApi.getVersions(versions).map {
            FapItemVersion(
                id = it.id,
                version = SemVer.fromString(it.version) ?: error("Failed parse ${it.version}")
            )
        }
    }
}
