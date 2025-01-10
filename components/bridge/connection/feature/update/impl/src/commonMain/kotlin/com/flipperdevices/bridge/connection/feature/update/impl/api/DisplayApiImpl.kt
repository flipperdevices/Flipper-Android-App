package com.flipperdevices.bridge.connection.feature.update.impl.api

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.update.api.DisplayApi
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.screen.ScreenFrame
import com.flipperdevices.protobuf.screen.StartVirtualDisplayRequest
import com.flipperdevices.protobuf.screen.StopVirtualDisplayRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okio.ByteString.Companion.toByteString

class DisplayApiImpl @AssistedInject constructor(
    @Assisted private val rpcFeatureApi: FRpcFeatureApi,
) : DisplayApi {
    override suspend fun startVirtualDisplay(byteArray: ByteArray): Result<Unit> {
        return rpcFeatureApi.requestOnce(
            Main(
                gui_start_virtual_display_request = StartVirtualDisplayRequest(
                    first_frame = ScreenFrame(
                        data_ = byteArray.toByteString()
                    )
                )
            ).wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).map { }
    }

    override suspend fun stopVirtualDisplay(): Result<Unit> {
        return rpcFeatureApi.requestOnce(
            Main(
                gui_stop_virtual_display_request = StopVirtualDisplayRequest()
            ).wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).map { }
    }
}
