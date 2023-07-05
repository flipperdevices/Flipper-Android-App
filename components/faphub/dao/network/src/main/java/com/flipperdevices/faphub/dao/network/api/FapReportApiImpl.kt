package com.flipperdevices.faphub.dao.network.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.dao.api.FapReportApi
import com.flipperdevices.faphub.dao.network.ktorfit.api.KtorfitApplicationApi
import com.flipperdevices.faphub.dao.network.ktorfit.model.KtorfitReport
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

private const val DEFAULT_TYPE = "default_android"

@ContributesBinding(AppGraph::class, FapReportApi::class)
class FapReportApiImpl @Inject constructor(
    val ktorfitApplicationApi: KtorfitApplicationApi
) : FapReportApi {
    override suspend fun report(applicationUid: String, description: String) {
        ktorfitApplicationApi.report(
            applicationUid,
            KtorfitReport(
                description = description,
                descriptionType = DEFAULT_TYPE
            )
        )
    }
}
