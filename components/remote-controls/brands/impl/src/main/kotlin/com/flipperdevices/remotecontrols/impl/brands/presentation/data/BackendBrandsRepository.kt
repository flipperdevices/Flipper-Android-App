package com.flipperdevices.remotecontrols.impl.brands.presentation.data

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.ifrmvp.api.infrared.FlipperInfraredBackendApi
import com.flipperdevices.ifrmvp.backend.model.BrandModel
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ContributesBinding(AppGraph::class, BrandsRepository::class)
class BackendBrandsRepository @Inject constructor(
    private val infraredBackendApi: FlipperInfraredBackendApi,
) : BrandsRepository {
    override suspend fun fetchBrands(
        categoryId: Long
    ): Result<List<BrandModel>> = withContext(FlipperDispatchers.workStealingDispatcher) {
        runCatching { infraredBackendApi.getManufacturers(categoryId).brands }
    }
}
