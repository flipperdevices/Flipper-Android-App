package com.flipperdevices.faphub.dao.network.ktorfit.api

import com.flipperdevices.faphub.dao.network.ktorfit.model.KtorfitCategory
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface KtorfitCategoryApi {
    @GET("v0/0/category")
    suspend fun getAll(
        @Query("api") sdkApi: String?,
        @Query("target") target: String?
    ): List<KtorfitCategory>
}
