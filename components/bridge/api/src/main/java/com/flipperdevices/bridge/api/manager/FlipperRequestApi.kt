package com.flipperdevices.bridge.api.manager

import com.flipperdevices.protobuf.Flipper
import kotlinx.coroutines.flow.Flow

interface FlipperRequestApi {
    fun notificationFlow(): Flow<Flipper.Main>
    fun request(command: Flipper.Main): Flow<Flipper.Main>
}