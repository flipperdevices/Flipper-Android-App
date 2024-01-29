package com.flipperdevices.faphub.dao.network.ktorfit.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement

interface KtorfitBundleApi {

    @Streaming
    @GET("v0/0/application/version/{uid}/build/compatible")
    suspend fun downloadBundle(
        @Path("uid") versionUid: String,
        @Query("target") target: String,
        @Query("api") sdkApi: String
    ): HttpStatement
}
