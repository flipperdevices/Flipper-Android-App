package com.flipperdevices.faphub.dao.network.retrofit.api

import com.flipperdevices.faphub.dao.network.retrofit.model.KtorfitApplicationShort
import com.flipperdevices.faphub.dao.network.retrofit.model.detailed.KtorfitApplicationDetailed
import com.flipperdevices.faphub.dao.network.retrofit.model.types.ApplicationSortType
import com.flipperdevices.faphub.dao.network.retrofit.model.types.SortOrderType
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

@Suppress("LongParameterList")
interface KtorfitApplicationApi {
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
        @Query("target")
        target: String? = null,
        @Query("api")
        sdkApiVersion: String? = null,
        @Query("category_id")
        categoryId: String? = null
    ): List<KtorfitApplicationShort>

    @GET("application/featured")
    suspend fun getFeaturedApps(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<KtorfitApplicationShort>

    @GET("application/{uid}")
    suspend fun get(@Path("uid") id: String): KtorfitApplicationDetailed
}
