package com.flipperdevices.metric.api.events.complex

import com.flipperdevices.metric.api.events.ComplexEvent

data class UpdateFlipperStart(
    private val updateFromVersion: String,
    private val updateToVersion: String,
    private val updateId: Int
) : ComplexEvent("update_flipper_start") {
    override fun getParamsMap(): Map<String, Any> {
        return mapOf(
            "update_from" to updateFromVersion,
            "update_to" to updateToVersion,
            "update_id" to updateId
        )
    }
}
