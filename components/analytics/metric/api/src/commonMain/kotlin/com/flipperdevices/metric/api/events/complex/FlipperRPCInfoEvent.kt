package com.flipperdevices.metric.api.events.complex

import com.flipperdevices.metric.api.events.ComplexEvent

data class FlipperRPCInfoEvent(
    val sdCardIsAvailable: Boolean,
    val internalFreeBytes: Long,
    val internalTotalBytes: Long,
    val externalFreeBytes: Long,
    val externalTotalBytes: Long,
    val firmwareForkName: String?,
    val firmwareGitUrl: String?
) : ComplexEvent("flipper_rpc_info") {
    override fun getParamsMap(): Map<String, Any?> {
        return mapOf(
            "sdcard_is_available" to sdCardIsAvailable,
            "internal_free_byte" to internalFreeBytes,
            "internal_total_byte" to internalTotalBytes,
            "external_free_byte" to externalFreeBytes,
            "external_total_byte" to externalTotalBytes,
            "firmware_fork_name" to firmwareForkName,
            "firmware_git_url" to firmwareGitUrl
        )
    }
}
