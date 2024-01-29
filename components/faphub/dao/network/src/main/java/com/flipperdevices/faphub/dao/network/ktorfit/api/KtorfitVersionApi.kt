package com.flipperdevices.faphub.dao.network.ktorfit.api

import com.flipperdevices.faphub.dao.network.ktorfit.model.KtorfitDetailedVersion
import com.flipperdevices.faphub.dao.network.ktorfit.model.requests.KtorfitDetailedVersionRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface KtorfitVersionApi {
    @POST("v0/1/application/versions")
    suspend fun getVersions(
        @Body versionRequest: KtorfitDetailedVersionRequest
    ): List<KtorfitDetailedVersion>
}
