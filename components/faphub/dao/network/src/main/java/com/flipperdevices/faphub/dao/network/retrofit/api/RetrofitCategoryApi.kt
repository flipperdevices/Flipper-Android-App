package com.flipperdevices.faphub.dao.network.retrofit.api

import com.flipperdevices.faphub.dao.network.retrofit.model.RetrofitCategory
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitCategoryApi {
    @GET("category/{uid}")
    suspend fun get(@Path("uid") id: String): RetrofitCategory

    @GET("category")
    suspend fun getAll(): List<RetrofitCategory>
}
