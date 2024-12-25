package com.flipperdevices.bridge.connection.feature.update.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.protobuf.system.RebootRequest

interface FUpdateFeatureApi : FDeviceFeatureApi {
    suspend fun startVirtualDisplay(byteArray: ByteArray): Result<Unit>
    suspend fun stopVirtualDisplay(): Result<Unit>
    suspend fun systemUpdate(updateManifestPath: String): Result<Unit>
    suspend fun reboot(mode: RebootRequest.RebootMode): Result<Unit>
}
