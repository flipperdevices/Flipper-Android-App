package com.flipperdevices.faphub.dao.network.retrofit.api

import com.flipperdevices.faphub.dao.network.retrofit.model.RetrofitApplicationDetailed
import com.flipperdevices.faphub.dao.network.retrofit.model.RetrofitApplicationShort
import com.flipperdevices.faphub.dao.network.retrofit.model.types.ApplicationSortType
import com.flipperdevices.faphub.dao.network.retrofit.model.types.SortOrderType
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@Suppress("LongParameterList")
interface RetrofitApplicationApi {
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
        applications: List<String>? = null
    ): List<RetrofitApplicationShort>

    @GET("application/featured")
    suspend fun getFeaturedApps(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<RetrofitApplicationShort>

    @GET("application/{uid}")
    suspend fun get(@Path("uid") id: String): RetrofitApplicationDetailed
}
