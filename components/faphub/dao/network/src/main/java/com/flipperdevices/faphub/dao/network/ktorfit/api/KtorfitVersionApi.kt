package com.flipperdevices.faphub.dao.network.ktorfit.api

import com.flipperdevices.faphub.dao.network.ktorfit.model.KtorfitDetailedVersion
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface KtorfitVersionApi {
    @GET("application/versions")
    suspend fun getVersions(
        @Query("uid") ids: List<String>
    ): List<KtorfitDetailedVersion>
}
