package com.flipperdevices.bridge.connection.feature.screenstreaming.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.protobuf.Main

interface FScreenUnlockFeatureApi : FDeviceFeatureApi {
    suspend fun unlock(): Result<Main>
}
