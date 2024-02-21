package com.flipperdevices.faphub.dao.network.ktorfit.api

import com.flipperdevices.faphub.dao.network.ktorfit.model.KtorfitApplicationShort
import com.flipperdevices.faphub.dao.network.ktorfit.model.KtorfitReport
import com.flipperdevices.faphub.dao.network.ktorfit.model.detailed.KtorfitApplicationDetailed
import com.flipperdevices.faphub.dao.network.ktorfit.model.requests.KtorfitApplicationApiRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

@Suppress("LongParameterList")
interface KtorfitApplicationApi {
    @POST("v0/1/application")
    suspend fun getAll(
        @Body applicationApiRequest: KtorfitApplicationApiRequest
    ): List<KtorfitApplicationShort>

    @GET("v0/0/application/featured")
    suspend fun getFeaturedApps(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<KtorfitApplicationShort>

    @GET("v0/0/application/{uid}")
    suspend fun get(
        @Path("uid")
        id: String,
        @Query("target")
        target: String? = null,
        @Query("api")
        sdkApiVersion: String? = null,
    ): KtorfitApplicationDetailed

    @POST("v0/0/application/{uid}/issue")
    suspend fun report(@Path("uid") applicationUid: String, @Body report: KtorfitReport)
}
