package com.flipperdevices.wearable.emulate.handheld.impl.task

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.google.android.gms.wearable.MessageEvent

interface WearableCommand {
    val path: String

    suspend fun processMessage(serviceApi: FlipperServiceApi, message: MessageEvent)
}
