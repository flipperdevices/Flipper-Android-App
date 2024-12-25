package com.flipperdevices.bridge.connection.feature.update.impl.api

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.update.api.FUpdateFeatureApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.Region
import com.flipperdevices.protobuf.screen.ScreenFrame
import com.flipperdevices.protobuf.screen.StartVirtualDisplayRequest
import com.flipperdevices.protobuf.screen.StopVirtualDisplayRequest
import com.flipperdevices.protobuf.system.PlayAudiovisualAlertRequest
import com.flipperdevices.protobuf.system.RebootRequest
import com.flipperdevices.protobuf.system.UpdateRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import okio.ByteString
import okio.ByteString.Companion.encode
import okio.ByteString.Companion.toByteString
import java.nio.charset.Charset

class FUpdateFeatureApiImpl @AssistedInject constructor(
    @Assisted private val rpcFeatureApi: FRpcFeatureApi,
) : FUpdateFeatureApi,
    LogTagProvider {
    override val TAG = "FAlarmFeatureApi"

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

    suspend fun region(finalCodeRegion: String, bands: List<Region.Band>) {
        Region(
            country_code = finalCodeRegion.encode(Charset.forName("ASCII")),
            bands = bands
        )
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            rpcFeatureApi: FRpcFeatureApi,
        ): FUpdateFeatureApiImpl
    }
}
