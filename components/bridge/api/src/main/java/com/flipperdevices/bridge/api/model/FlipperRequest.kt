package com.flipperdevices.bridge.api.model

import com.flipperdevices.protobuf.Flipper

data class FlipperRequest(
    val data: Flipper.Main,
    val createTimestampNanos: Long = System.nanoTime(),
    val priority: FlipperRequestPriority = FlipperRequestPriority.DEFAULT,
    val onSendCallback: (() -> Unit)? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FlipperRequest

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }
}

fun Flipper.Main.wrapToRequest(
    priority: FlipperRequestPriority = FlipperRequestPriority.DEFAULT
) = FlipperRequest(data = this, priority = priority)
