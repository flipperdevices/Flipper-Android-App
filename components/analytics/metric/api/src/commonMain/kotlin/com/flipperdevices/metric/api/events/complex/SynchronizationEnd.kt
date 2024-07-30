package com.flipperdevices.metric.api.events.complex

import com.flipperdevices.metric.api.events.ComplexEvent

data class SynchronizationEnd(
    val subghzCount: Int,
    val rfidCount: Int,
    val nfcCount: Int,
    val infraredCount: Int,
    val iButtonCount: Int,
    val synchronizationTimeMs: Long,
    val changesCount: Int
) : ComplexEvent("synchronization_end") {
    override fun getParamsMap(): Map<String, Any> {
        return mapOf(
            "subghz_count" to subghzCount,
            "rfid_count" to rfidCount,
            "nfc_count" to nfcCount,
            "infrared_count" to infraredCount,
            "ibutton_count" to iButtonCount,
            "synchronization_time_ms" to synchronizationTimeMs,
            "changes_count" to changesCount
        )
    }
}
