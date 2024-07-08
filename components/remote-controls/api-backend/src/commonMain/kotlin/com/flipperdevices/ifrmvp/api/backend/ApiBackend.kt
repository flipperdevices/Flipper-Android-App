package com.flipperdevices.ifrmvp.api.backend

import com.flipperdevices.ifrmvp.backend.model.BrandsResponse
import com.flipperdevices.ifrmvp.backend.model.CategoriesResponse
import com.flipperdevices.ifrmvp.backend.model.IfrFileContentResponse
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import com.flipperdevices.ifrmvp.model.PagesLayout

interface ApiBackend {
    suspend fun getCategories(): CategoriesResponse
    suspend fun getManufacturers(categoryId: Long): BrandsResponse
    suspend fun getSignal(request: SignalRequestModel): SignalResponseModel
    suspend fun getIfrFileContent(ifrFileId: Long): IfrFileContentResponse
    suspend fun getUiFile(ifrFileId: Long): PagesLayout
}
