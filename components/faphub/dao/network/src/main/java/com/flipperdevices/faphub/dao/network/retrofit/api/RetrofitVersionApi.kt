package com.flipperdevices.faphub.dao.network.retrofit.api

import com.flipperdevices.faphub.dao.network.retrofit.model.RetrofitDetailedVersion
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitVersionApi {
    @GET("application/versions")
    suspend fun getVersions(
        @Query("uid") ids: List<String>
    ): List<RetrofitDetailedVersion>
}