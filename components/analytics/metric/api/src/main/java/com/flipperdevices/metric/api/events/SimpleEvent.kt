package com.flipperdevices.metric.api.events

enum class SimpleEvent(val id: String) {
    APP_OPEN("app_open"),
    OPEN_SAVE_KEY("open_save_key"),
    OPEN_EMULATE("open_emulate"),
    OPEN_EDIT("open_edit"),
    OPEN_SHARE("open_share"),
    EXPERIMENTAL_OPEN_FM("experimental_open_fm"),
    EXPERIMENTAL_OPEN_SCREENSTREAMING("experimental_open_screenstreaming"),
}
