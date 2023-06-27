package com.flipperdevices.faphub.dao.network.retrofit.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement

interface KtorfitBundleApi {

    @Streaming
    @GET("/api/v0/application/{uid}/build/compatible")
    suspend fun downloadBundle(
        @Path("uid") appUid: String,
        @Query("target") target: String,
        @Query("api") sdkApi: String
    ): HttpStatement
}
