package com.flipperdevices.remotecontrols.impl.categories.presentation.data

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.ifrmvp.api.infrared.FlipperInfraredBackendApi
import com.flipperdevices.ifrmvp.backend.model.DeviceCategory
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ContributesBinding(AppGraph::class, DeviceCategoriesRepository::class)
class BackendDeviceCategoriesRepository @Inject constructor(
    private val infraredBackendApi: FlipperInfraredBackendApi,
) : DeviceCategoriesRepository {

    override suspend fun fetchCategories(): Result<List<DeviceCategory>> = runCatching {
        withContext(FlipperDispatchers.workStealingDispatcher) {
            infraredBackendApi.getCategories().categories
        }
    }
}
