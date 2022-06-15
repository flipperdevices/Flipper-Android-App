package com.flipperdevices.metric.api.events.complex

import com.flipperdevices.metric.api.events.ComplexEvent

data class FlipperGattInfoEvent(
    private val flipperVersion: String
) : ComplexEvent("flipper_gatt_info") {
    override fun getParamsMap(): Map<String, Any> {
        return mapOf(
            "flipper_version" to flipperVersion
        )
    }
}
