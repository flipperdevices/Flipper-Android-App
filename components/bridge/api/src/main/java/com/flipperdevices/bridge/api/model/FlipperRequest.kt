package com.flipperdevices.bridge.api.model

import com.flipperdevices.protobuf.Flipper

data class FlipperRequest(
    val data: Flipper.Main,
)

fun Flipper.Main.wrapToRequest() = FlipperRequest(data = this)
