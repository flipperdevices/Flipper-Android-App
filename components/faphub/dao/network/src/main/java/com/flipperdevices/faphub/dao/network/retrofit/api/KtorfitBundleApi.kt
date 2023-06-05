package com.flipperdevices.faphub.dao.network.retrofit.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement

interface KtorfitBundleApi {

    @Streaming
    @GET("application/version/{uid}/build/{target}/{api}")
    suspend fun downloadBundle(
        @Path("uid") versionId: String,
        @Path("target") target: String,
        @Path("api") sdkApi: String
    ): HttpStatement
}
