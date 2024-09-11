package com.flipperdevices.bridge.connection.feature.storage.impl

import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDownloadApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileStorageMD5Api
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi

class FStorageFeatureApiImpl(
    private val md5Api: FFileStorageMD5Api,
    private val fListingStorageApi: FListingStorageApi,
    private val fileUploadApi: FFileUploadApi,
    private val fileDownloadApi: FFileDownloadApi
) : FStorageFeatureApi {
    override fun md5Api() = md5Api
    override fun listingApi() = fListingStorageApi
    override fun uploadApi() = fileUploadApi
    override fun downloadApi() = fileDownloadApi
}