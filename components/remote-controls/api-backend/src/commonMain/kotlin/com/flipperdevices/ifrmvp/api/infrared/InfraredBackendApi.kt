package com.flipperdevices.ifrmvp.api.infrared

import com.flipperdevices.ifrmvp.backend.model.BrandsResponse
import com.flipperdevices.ifrmvp.backend.model.CategoriesResponse
import com.flipperdevices.ifrmvp.backend.model.IfrFileContentResponse
import com.flipperdevices.ifrmvp.backend.model.PagesLayoutBackendModel
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

interface InfraredBackendApi {
    @GET("categories")
    suspend fun getCategories(): CategoriesResponse

    @GET("brands")
    suspend fun getManufacturers(
        @Query("category_id") categoryId: Long
    ): BrandsResponse

    @POST("signal")
    suspend fun getSignal(
        @Body request: SignalRequestModel
    ): SignalResponseModel

    @GET("key")
    suspend fun getIfrFileContent(
        @Query("ifr_file_id") ifrFileId: Long
    ): IfrFileContentResponse

    @GET("ui")
    suspend fun getUiFile(
        @Query("ifr_file_id") ifrFileId: Long
    ): PagesLayoutBackendModel
}
