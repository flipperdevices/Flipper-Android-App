package com.flipperdevices.bridge.impl.manager.service.request

import com.flipperdevices.bridge.api.utils.Constants
import kotlin.math.min
import no.nordicsemi.android.ble.data.DataSplitter

/**
 * Split message to chunks smaller than MTU
 */
class FixedSizeDataSplitter(
    private val maxChunkSize: Int = Constants.BLE.MAX_PAYLOAD_SIZE
) : DataSplitter {
    override fun chunk(message: ByteArray, index: Int, maxLength: Int): ByteArray? {
        val chunkSize = min(maxChunkSize, maxLength)
        val offset = index * chunkSize
        val length = min(chunkSize, message.size - offset)

        if (length <= 0) return null

        return message.copyOfRange(offset, offset + length)
    }
}
