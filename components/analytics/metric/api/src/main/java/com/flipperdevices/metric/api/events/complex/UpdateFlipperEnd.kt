package com.flipperdevices.metric.api.events.complex

import com.flipperdevices.metric.api.events.ComplexEvent

data class UpdateFlipperEnd(
    val updateFrom: String,
    val updateTo: String,
    val updateId: Long,
    val updateStatus: UpdateStatus
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
    COMPLETED(id = 1),
    CANCELED(id = 2),
    FAILED_DOWNLOAD(id = 3),
    FAILED_PREPARE(id = 4),
    FAILED_UPLOAD(id = 5),
    FAILED(id = 6)
}
