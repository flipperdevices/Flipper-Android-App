package com.flipperdevices.faphub.dao.network.ktorfit.api

import com.flipperdevices.faphub.dao.network.ktorfit.model.KtorfitApplicationShort
import com.flipperdevices.faphub.dao.network.ktorfit.model.KtorfitReport
import com.flipperdevices.faphub.dao.network.ktorfit.model.detailed.KtorfitApplicationDetailed
import com.flipperdevices.faphub.dao.network.ktorfit.model.types.ApplicationSortType
import com.flipperdevices.faphub.dao.network.ktorfit.model.types.SortOrderType
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

@Suppress("LongParameterList")
interface KtorfitApplicationApi {
    @GET("application")
    suspend fun getAllWithTarget(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("query") query: String? = null,
        @Query("sort_by")
        @ApplicationSortType
        sortBy: String? = null,
        @Query("sort_order")
        @SortOrderType
        sortOrder: Int? = null,
        @Query("applications")
        applications: List<String>? = null,
        @Query("target")
        target: String,
        @Query("api")
        sdkApiVersion: String,
        @Query("category_id")
        categoryId: String? = null
    ): List<KtorfitApplicationShort>

    @GET("application")
    suspend fun getAll(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("query") query: String? = null,
        @Query("sort_by")
        @ApplicationSortType
        sortBy: String? = null,
        @Query("sort_order")
        @SortOrderType
        sortOrder: Int? = null,
        @Query("applications")
        applications: List<String>? = null,
        @Query("category_id")
        categoryId: String? = null,
        @Query("is_latest_version")
        isLatestVersion: Boolean = true
    ): List<KtorfitApplicationShort>

    @GET("application/featured")
    suspend fun getFeaturedApps(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<KtorfitApplicationShort>

    @GET("application/{uid}")
    suspend fun get(
        @Path("uid")
        id: String,
        @Query("target")
        target: String? = null,
        @Query("api")
        sdkApiVersion: String? = null,
    ): KtorfitApplicationDetailed

    @POST("application/{uid}/issue")
    suspend fun report(@Path("uid") applicationUid: String, @Body report: KtorfitReport)
}
