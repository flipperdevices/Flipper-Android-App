package com.flipperdevices.metric.api.events.complex

import com.flipperdevices.metric.api.events.ComplexEvent

data class UpdateFlipperEnd(
    private val updateFrom: String,
    private val updateTo: String,
    private val updateId: Int,
    private val updateStatus: UpdateStatus
) : ComplexEvent("update_flipper_end") {
    override fun getParamsMap(): Map<String, Any> {
        return mapOf(
            "update_from" to updateFrom,
            "update_to" to updateTo,
            "update_id" to updateId,
            "update_status" to updateStatus.id
        )
    }
}

enum class UpdateStatus(val id: Int) {
    COMPLETED(1),
    CANCELED(2),
    FAILED_DOWNLOAD(3),
    FAILED_PREPARE(4),
    FAILED_UPLOAD(5),
    FAILED(6)
}
