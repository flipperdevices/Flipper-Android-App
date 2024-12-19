package com.flipperdevices.bridge.connection.feature.screenstreaming.impl.api

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.screenstreaming.api.FScreenStreamingFeatureApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.screen.InputKey
import com.flipperdevices.protobuf.screen.InputType
import com.flipperdevices.protobuf.screen.ScreenFrame
import com.flipperdevices.protobuf.screen.SendInputEventRequest
import com.flipperdevices.protobuf.screen.StartScreenStreamRequest
import com.flipperdevices.protobuf.screen.StopScreenStreamRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class FScreenStreamingFeatureApiImpl @AssistedInject constructor(
    @Assisted private val rpcFeatureApi: FRpcFeatureApi,
) : FScreenStreamingFeatureApi,
    LogTagProvider {
    override val TAG = "FScreenStreamingFeatureApi"

    override suspend fun sendInputAndForget(inputKey: InputKey, inputType: InputType) {
        return rpcFeatureApi.requestWithoutAnswer(
            Main(
                gui_send_input_event_request = SendInputEventRequest(
                    key = inputKey,
                    type = inputType
                )
            ).wrapToRequest()
        )
    }

    override suspend fun awaitInput(inputKey: InputKey, inputType: InputType): Flow<Result<Main>> {
        return rpcFeatureApi.request(
            Main(
                gui_send_input_event_request = SendInputEventRequest(
                    key = inputKey,
                    type = inputType
                )
            ).wrapToRequest()
        )
    }

    override suspend fun stop(): Result<Main> {
        return rpcFeatureApi.requestOnce(
            Main(
                gui_stop_screen_stream_request = StopScreenStreamRequest()
            ).wrapToRequest()
        )
    }

    override suspend fun guiScreenFrameFlow(): Flow<ScreenFrame> {
        return rpcFeatureApi.request(
            Main(
                gui_start_screen_stream_request = StartScreenStreamRequest()
            ).wrapToRequest()
        ).mapNotNull { result -> result.getOrNull()?.gui_screen_frame }
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            rpcFeatureApi: FRpcFeatureApi,
        ): FScreenStreamingFeatureApiImpl
    }
}
