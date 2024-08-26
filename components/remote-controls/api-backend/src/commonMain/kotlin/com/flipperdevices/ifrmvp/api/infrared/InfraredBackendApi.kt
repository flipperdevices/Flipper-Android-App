package com.flipperdevices.ifrmvp.api.infrared

import com.flipperdevices.ifrmvp.backend.model.BrandsResponse
import com.flipperdevices.ifrmvp.backend.model.CategoriesResponse
import com.flipperdevices.ifrmvp.backend.model.IfrFileContentResponse
import com.flipperdevices.ifrmvp.backend.model.InfraredsResponse
import com.flipperdevices.ifrmvp.backend.model.PagesLayoutBackendModel
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel

interface InfraredBackendApi {
    suspend fun getCategories(): CategoriesResponse

    suspend fun getManufacturers(categoryId: Long): BrandsResponse

    suspend fun getSignal(request: SignalRequestModel): SignalResponseModel

    suspend fun getIfrFileContent(ifrFileId: Long): IfrFileContentResponse

    suspend fun getUiFile(ifrFileId: Long): PagesLayoutBackendModel

    suspend fun getInfrareds(brandId: Long): InfraredsResponse
}
