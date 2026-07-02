package com.flipperdevices.remotecontrols.impl.categories.presentation.data

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.ifrmvp.api.infrared.FlipperInfraredBackendApi
import com.flipperdevices.ifrmvp.backend.model.DeviceCategory
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.withContext
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<DeviceCategoriesRepository>())
class BackendDeviceCategoriesRepository @Inject constructor(
    private val infraredBackendApi: FlipperInfraredBackendApi,
) : DeviceCategoriesRepository {

    override suspend fun fetchCategories(): Result<List<DeviceCategory>> = runCatching {
        withContext(FlipperDispatchers.workStealingDispatcher) {
            infraredBackendApi.getCategories().categories
        }
    }
}
