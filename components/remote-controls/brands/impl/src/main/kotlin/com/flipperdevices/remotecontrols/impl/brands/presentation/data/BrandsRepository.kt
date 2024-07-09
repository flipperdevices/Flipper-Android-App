package com.flipperdevices.remotecontrols.impl.brands.presentation.data

import com.flipperdevices.ifrmvp.backend.model.BrandModel

interface BrandsRepository {
    suspend fun fetchBrands(categoryId: Long): Result<List<BrandModel>>
}
