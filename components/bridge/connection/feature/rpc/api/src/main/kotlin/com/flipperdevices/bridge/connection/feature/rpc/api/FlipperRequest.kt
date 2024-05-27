package com.flipperdevices.bridge.connection.feature.rpc.api

import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.core.ktx.jre.TimeHelper
import com.flipperdevices.protobuf.Flipper

data class FlipperRequest(
    val data: Flipper.Main,
    val createTimestampNanos: Long = TimeHelper.getNanoTime(),
    val priority: FlipperRequestPriority = FlipperRequestPriority.DEFAULT
)

fun Flipper.Main.wrapToRequest(
    priority: FlipperRequestPriority = FlipperRequestPriority.DEFAULT
) = FlipperRequest(data = this, priority = priority)
