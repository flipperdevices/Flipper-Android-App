package com.flipperdevices.updater.impl.tasks

import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.screen.screenFrame
import com.flipperdevices.protobuf.screen.startVirtualDisplayRequest
import com.flipperdevices.protobuf.screen.stopVirtualDisplayRequest
import com.google.protobuf.ByteString
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import com.flipperdevices.core.ui.res.R as DesignSystem

private const val STOP_IMAGE_TIMEOUT_MS = 5 * 1000L

class FlipperUpdateImageHelper(
    private val context: Context
) : LogTagProvider {
    override val TAG = "FlipperUpdateImageHelper"

    suspend fun loadImageOnFlipper(
        requestApi: FlipperRequestApi
    ) {
        info { "Start streaming" }
        val bytes = loadImageFromResource()
        requestApi.request(
            main {
                guiStartVirtualDisplayRequest = startVirtualDisplayRequest {
                    firstFrame = screenFrame {
                        data = ByteString.copyFrom(bytes)
                    }
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).first()
    }

    suspend fun stopImageOnFlipperSafe(
        requestApi: FlipperRequestApi
    ) = try {
        info { "Request stop streaming" }
        withTimeout(STOP_IMAGE_TIMEOUT_MS) {
            requestApi.request(
                main {
                    guiStopVirtualDisplayRequest = stopVirtualDisplayRequest { }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            ).first()
        }
    } catch (e: Exception) {
        error(e) { "Error while stop streaming" }
    }

    private suspend fun loadImageFromResource(): ByteArray =
        withContext(FlipperDispatchers.workStealingDispatcher) {
            val pictureStream = context.resources.openRawResource(DesignSystem.raw.update_pic)
            return@withContext pictureStream.use { inputStream ->
                inputStream.readBytes()
            }
        }
}
