package com.flipperdevices.faphub.dao.network.retrofit.api

import com.flipperdevices.faphub.dao.network.retrofit.model.KtorfitCategory
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface KtorfitCategoryApi {
    @GET("category/{uid}")
    suspend fun get(@Path("uid") id: String): KtorfitCategory

    @GET("category")
    suspend fun getAll(): List<KtorfitCategory>
}
