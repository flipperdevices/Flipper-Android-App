package com.flipperdevices.faphub.dao.network.retrofit.api

import com.flipperdevices.faphub.dao.network.retrofit.model.KtorfitCategory
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface KtorfitCategoryApi {
    @GET("category")
    suspend fun getAll(
        @Query("api") sdkApi: String?
    ): List<KtorfitCategory>
}
