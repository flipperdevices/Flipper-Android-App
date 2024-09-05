package com.flipperdevices.bridge.connection.feature.storage.impl

import com.flipperdevices.bridge.connection.feature.storage.api.FFileStorageApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileStorageMD5Api

class FFileStorageApiImpl(
    private val md5Api: FFileStorageMD5Api
) : FFileStorageApi, FFileStorageMD5Api by md5Api {
}