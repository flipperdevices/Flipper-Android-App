package com.flipperdevices.updater.api

import com.flipperdevices.updater.model.FirmwareVersion
import kotlinx.coroutines.flow.Flow

interface FlipperVersionProviderApi {
    fun getCurrentFlipperVersion(): Flow<FirmwareVersion?>
}
