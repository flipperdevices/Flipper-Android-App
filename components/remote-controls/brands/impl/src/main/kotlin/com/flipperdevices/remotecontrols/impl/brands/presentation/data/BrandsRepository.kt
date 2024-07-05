package com.flipperdevices.remotecontrols.impl.brands.presentation.data

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.api.backend.ApiBackend
import com.flipperdevices.ifrmvp.backend.model.BrandModel
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface BrandsRepository {
    suspend fun fetchBrands(categoryId: Long): Result<List<BrandModel>>
}

@ContributesBinding(AppGraph::class, BrandsRepository::class)
class BackendBrandsRepository @Inject constructor(
    private val apiBackend: ApiBackend,
) : BrandsRepository {
    override suspend fun fetchBrands(
        categoryId: Long
    ): Result<List<BrandModel>> = withContext(Dispatchers.IO) {
        runCatching {
            apiBackend.getManufacturers(categoryId).brands
        }
    }
}
