package com.flipperdevices.bridge.connection.feature.storage.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDownloadApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileStorageMD5Api
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi

interface FStorageFeatureApi : FDeviceFeatureApi {
    fun md5Api(): FFileStorageMD5Api
    fun listingApi(): FListingStorageApi
    fun downloadApi(): FFileDownloadApi
    fun uploadApi(): FFileUploadApi
}