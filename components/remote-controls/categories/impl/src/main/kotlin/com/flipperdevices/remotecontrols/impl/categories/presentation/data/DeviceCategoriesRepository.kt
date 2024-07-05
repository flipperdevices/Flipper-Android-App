package com.flipperdevices.remotecontrols.impl.categories.presentation.data

import com.flipperdevices.ifrmvp.backend.model.DeviceCategory

interface DeviceCategoriesRepository {
    suspend fun fetchCategories(): Result<List<DeviceCategory>>
}
