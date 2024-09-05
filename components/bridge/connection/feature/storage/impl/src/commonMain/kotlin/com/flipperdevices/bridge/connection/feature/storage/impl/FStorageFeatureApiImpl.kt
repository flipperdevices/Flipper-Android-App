package com.flipperdevices.bridge.connection.feature.storage.impl

import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileStorageMD5Api

class FStorageFeatureApiImpl(
    private val md5Api: FFileStorageMD5Api
) : FStorageFeatureApi {
    override fun md5Api() = md5Api
}