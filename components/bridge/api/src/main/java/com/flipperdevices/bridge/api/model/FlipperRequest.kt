package com.flipperdevices.bridge.api.model

import com.flipperdevices.protobuf.Flipper

data class FlipperRequest(
    val data: Flipper.Main,
    val createTimestampNanos: Long = System.nanoTime(),
    val priority: FlipperRequestPriority = FlipperRequestPriority.DEFAULT,
    val onSendCallback: (() -> Unit)? = null
)

fun Flipper.Main.wrapToRequest(
    priority: FlipperRequestPriority = FlipperRequestPriority.DEFAULT
) = FlipperRequest(data = this, priority = priority)
