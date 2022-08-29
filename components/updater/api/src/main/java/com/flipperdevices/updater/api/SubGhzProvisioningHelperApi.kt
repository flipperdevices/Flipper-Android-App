package com.flipperdevices.updater.api

import com.flipperdevices.bridge.service.api.FlipperServiceApi

interface SubGhzProvisioningHelperApi {
    suspend fun provideAndUploadSubGhz(serviceApi: FlipperServiceApi)
    suspend fun getRegion(): String?
}
