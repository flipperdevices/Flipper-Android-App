package com.flipperdevices.updater.impl.tasks

import android.content.Context
import com.flipperdevices.bridge.connection.feature.update.api.FUpdateFeatureApi
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import com.flipperdevices.core.ui.res.R as DesignSystem

private const val STOP_IMAGE_TIMEOUT_MS = 5 * 1000L

class FlipperUpdateImageHelper @Inject constructor(
    private val context: Context,
) : LogTagProvider {
    override val TAG = "FlipperUpdateImageHelper"

    suspend fun loadImageOnFlipper(fUpdateFeatureApi: FUpdateFeatureApi) {
        info { "Start streaming" }
        val bytes = loadImageFromResource()
        fUpdateFeatureApi.startVirtualDisplay(bytes)
            .onFailure { error(it) { "#loadImageOnFlipper could not start virtual display" } }
    }

    suspend fun stopImageOnFlipperSafe(fUpdateFeatureApi: FUpdateFeatureApi) = try {
        info { "Request stop streaming" }
        withTimeout(STOP_IMAGE_TIMEOUT_MS) {
            fUpdateFeatureApi.stopVirtualDisplay()
                .onFailure { error(it) { "#loadImageOnFlipper could not stop virtual display" } }
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
