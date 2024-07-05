package com.flipperdevices.remotecontrols.impl.categories.presentation.data

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.api.backend.ApiBackend
import com.flipperdevices.ifrmvp.backend.model.DeviceCategory
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import javax.inject.Inject
import javax.inject.Singleton

@ContributesBinding(AppGraph::class, DeviceCategoriesRepository::class)
class BackendDeviceCategoriesRepository @Inject constructor(
    private val apiBackend: ApiBackend,
) : DeviceCategoriesRepository {

    override suspend fun fetchCategories(): Result<List<DeviceCategory>> = runCatching {
        withContext(Dispatchers.IO) {
            apiBackend.getCategories().categories
        }
    }.onFailure(Throwable::printStackTrace)
}
