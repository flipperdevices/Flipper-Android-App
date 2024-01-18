package com.flipperdevices.metric.api.events.complex

import com.flipperdevices.metric.api.events.ComplexEvent

enum class DebugInfoEnum(val key: String) {
    NFC_UNSUPPORTED_EDIT("nfc_unsupported_format")
}

data class DebugInfoEvent(
    val key: DebugInfoEnum,
    val value: String
) : ComplexEvent("debug") {
    override fun getParamsMap(): Map<String, Any> {
        return mapOf(
            "key" to key.key,
            "value" to value
        )
    }
}
