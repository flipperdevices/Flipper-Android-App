package com.flipperdevices.bridge.connection.feature.update.impl.api

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.update.api.BootApi
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.system.RebootRequest
import com.flipperdevices.protobuf.system.UpdateRequest

class BootApiImpl(
    private val rpcFeatureApi: FRpcFeatureApi
) : BootApi {
    override suspend fun systemUpdate(updateManifestPath: String): Result<Unit> {
        return rpcFeatureApi.requestOnce(
            Main(
                system_update_request = UpdateRequest(
                    update_manifest = updateManifestPath
                )
            ).wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).map { }
    }

    override suspend fun reboot(mode: RebootRequest.RebootMode): Result<Unit> {
        return rpcFeatureApi.requestOnce(
            Main(
                system_reboot_request = RebootRequest(
                    mode = mode
                )
            ).wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).map { }
    }
}
