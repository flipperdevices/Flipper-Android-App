package com.flipperdevices.bridge.api.model

import com.flipperdevices.core.ktx.jre.TimeHelper
import com.flipperdevices.protobuf.Flipper

data class FlipperRequest(
    val data: Flipper.Main,
    val createTimestampNanos: Long = TimeHelper.getNanoTime(),
    val priority: FlipperRequestPriority = FlipperRequestPriority.DEFAULT,
    val onSendCallback: SendCallback? = null
)

fun interface SendCallback {
    suspend fun onSendCallback()
}

fun Flipper.Main.wrapToRequest(
    priority: FlipperRequestPriority = FlipperRequestPriority.DEFAULT
) = FlipperRequest(data = this, priority = priority)
