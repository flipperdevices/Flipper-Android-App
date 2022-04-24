package com.flipperdevices.bridge.api.model

/**
 * Highest value processed first
 *
 * Enum order is important
 */
enum class FlipperRequestPriority {
    RIGHT_NOW,
    FOREGROUND,
    DEFAULT,
    BACKGROUND
}
