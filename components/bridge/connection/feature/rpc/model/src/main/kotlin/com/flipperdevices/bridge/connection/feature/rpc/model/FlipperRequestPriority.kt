package com.flipperdevices.bridge.connection.feature.rpc.model

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
