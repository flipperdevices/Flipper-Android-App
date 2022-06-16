package com.flipperdevices.metric.api.events.complex

import com.flipperdevices.metric.api.events.ComplexEvent

data class FlipperRPCInfoEvent(
    private val sdCardIsAvailable: Boolean,
    private val internalFreeBytes: Int,
    private val internalTotalBytes: Int,
    private val externalFreeBytes: Int,
    private val externalTotalBytes: Int
) : ComplexEvent("flipper_rpc_info") {
    override fun getParamsMap(): Map<String, Any> {
        return mapOf(
            "sdcard_is_available" to sdCardIsAvailable,
            "internal_free_byte" to internalFreeBytes,
            "internal_total_byte" to internalTotalBytes,
            "external_free_byte" to externalFreeBytes,
            "external_total_byte" to externalTotalBytes
        )
    }
}
