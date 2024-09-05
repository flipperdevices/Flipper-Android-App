package com.flipperdevices.bridge.connection.feature.storage.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileStorageMD5Api

interface FFileStorageApi : FDeviceFeatureApi, FFileStorageMD5Api {
}