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

internal class LocalBrandsRepository : BrandsRepository {
    override suspend fun fetchBrands(categoryId: Long): Result<List<BrandModel>> = runCatching {
        if (Random.nextBoolean()) error("Some random error")
        var i = 0L
        listOf(
            BrandModel(i++, "123TV", i++),
            BrandModel(i++, "1TV", i++),
            BrandModel(i++, "77", i++),
            BrandModel(i++, "LG", i++),
            BrandModel(i++, "Abex", i++),
            BrandModel(i++, "Acer", i++),
            BrandModel(i++, "ACL", i++),
            BrandModel(i++, "Across", i++),
            BrandModel(i++, "Acme", i++),
            BrandModel(i++, "Adc", i++),
            BrandModel(i++, "Advert", i++),
        )
    }
}
