package com.flipperdevices.widget.impl.model

import dagger.MapKey

enum class WidgetState {
    NOT_INITIALIZE,
    PENDING,
    IN_PROGRESS,
    ERROR_OUT_OF_RANGE,
    ERROR_NOT_SYNCED,
    ERROR_BT_NOT_ENABLED,
    ERROR_FLIPPER_BUSY,
    ERROR_KEY_DELETED,
    ERROR_UNKNOWN
}

@MapKey
@Retention(AnnotationRetention.RUNTIME)
annotation class WidgetRendererOf(
    val value: WidgetState
)
