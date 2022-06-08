package com.flipperdevices.updater.impl.tasks

import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.screen.screenFrame
import com.flipperdevices.protobuf.screen.startVirtualDisplayRequest
import com.flipperdevices.updater.impl.R
import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class FlipperUpdateImageHelper(
    private val context: Context
) {
    suspend fun loadImageOnFlipper(
        requestApi: FlipperRequestApi
    ) {
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

    private suspend fun loadImageFromResource(): ByteArray = withContext(Dispatchers.IO) {
        return@withContext context.resources.openRawResource(R.raw.update_pic).use { inputStream ->
            inputStream.readBytes()
        }
    }
}
