package com.flipperdevices.metric.api.events

abstract class ComplexEvent(val id: String) {
    abstract fun getParamsMap(): Map<String, Any?>
}
