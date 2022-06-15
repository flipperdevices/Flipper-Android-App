package com.flipperdevices.metric.api.events.complex

import com.flipperdevices.metric.api.events.ComplexEvent

data class SynchronizationEnd(
    private val subghzCount: Int,
    private val rfidCount: Int,
    private val nfcCount: Int,
    private val infraredCount: Int,
    private val iButtonCount: Int,
    private val synchronizationTimeMs: Int
) : ComplexEvent("synchronization_end") {
    override fun getParamsMap(): Map<String, Any> {
        return mapOf(
            "subghz_count" to subghzCount,
            "rfid_count" to rfidCount,
            "nfc_count" to nfcCount,
            "infrared_count" to infraredCount,
            "ibutton_count" to iButtonCount,
            "synchronization_time_ms" to synchronizationTimeMs
        )
    }
}
