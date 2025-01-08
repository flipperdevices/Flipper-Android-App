package com.flipperdevices.bridge.connection.feature.update.api

import com.flipperdevices.protobuf.system.RebootRequest

interface BootApi {
    suspend fun systemUpdate(updateManifestPath: String): Result<Unit>
    suspend fun reboot(mode: RebootRequest.RebootMode): Result<Unit>
}
