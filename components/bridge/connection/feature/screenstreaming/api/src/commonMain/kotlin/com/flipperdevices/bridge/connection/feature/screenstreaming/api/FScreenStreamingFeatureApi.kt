package com.flipperdevices.bridge.connection.feature.screenstreaming.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.screen.InputKey
import com.flipperdevices.protobuf.screen.InputType
import com.flipperdevices.protobuf.screen.ScreenFrame
import kotlinx.coroutines.flow.Flow

interface FScreenStreamingFeatureApi : FDeviceFeatureApi {
    suspend fun awaitInput(inputKey: InputKey, inputType: InputType): Flow<Result<Main>>

    suspend fun sendInputAndForget(inputKey: InputKey, inputType: InputType)

    suspend fun stop(): Result<Main>

    /**
     * Start collect [ScreenFrame].
     * Need to call [stop] to cancel data sending from flipper
     */
    suspend fun guiScreenFrameFlow(): Flow<ScreenFrame>
}
