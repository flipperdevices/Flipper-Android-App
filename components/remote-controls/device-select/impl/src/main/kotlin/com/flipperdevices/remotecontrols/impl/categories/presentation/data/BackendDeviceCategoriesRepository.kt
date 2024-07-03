package com.flipperdevices.remotecontrols.impl.categories.presentation.data

import com.flipperdevices.ifrmvp.api.backend.ApiBackend
import com.flipperdevices.ifrmvp.backend.model.DeviceCategory
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class BackendDeviceCategoriesRepository(
    private val apiBackend: ApiBackend,
    private val ioDispatcher: CoroutineContext
) : DeviceCategoriesRepository {

    override suspend fun fetchCategories(): Result<List<DeviceCategory>> = runCatching {
        withContext(ioDispatcher) {
            apiBackend.getCategories().categories
        }
    }.onFailure(Throwable::printStackTrace)
}
