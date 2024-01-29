package com.flipperdevices.metric.api.events.complex

import com.flipperdevices.metric.api.events.ComplexEvent

data class UpdateFlipperStart(
    val updateFromVersion: String,
    val updateToVersion: String,
    val updateId: Long
) : ComplexEvent("update_flipper_start") {
    override fun getParamsMap(): Map<String, Any> {
        return mapOf(
            "update_from" to updateFromVersion,
            "update_to" to updateToVersion,
            "update_id" to updateId
        )
    }
}
