package com.flipperdevices.faphub.dao.network.retrofit.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface RetrofitBundleApi {

    @Streaming
    @GET("application/version/{uid}/build/{target}/{api}")
    suspend fun downloadBundle(
        @Path("uid") versionId: String,
        @Path("target") target: String,
        @Path("api") sdkApi: String
    ): ResponseBody
}