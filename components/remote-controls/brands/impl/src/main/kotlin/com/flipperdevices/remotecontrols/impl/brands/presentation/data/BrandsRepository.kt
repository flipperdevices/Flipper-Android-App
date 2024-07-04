package com.flipperdevices.remotecontrols.impl.brands.presentation.data

import com.flipperdevices.ifrmvp.api.backend.ApiBackend
import com.flipperdevices.ifrmvp.backend.model.BrandModel
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

internal interface BrandsRepository {
    suspend fun fetchBrands(categoryId: Long): Result<List<BrandModel>>
}

internal class BackendBrandsRepository(
    private val apiBackend: ApiBackend,
    private val ioDispatcher: CoroutineContext
) : BrandsRepository {
    override suspend fun fetchBrands(
        categoryId: Long
    ): Result<List<BrandModel>> = withContext(ioDispatcher) {
        runCatching {
            apiBackend.getManufacturers(categoryId).brands
        }
    }
}
