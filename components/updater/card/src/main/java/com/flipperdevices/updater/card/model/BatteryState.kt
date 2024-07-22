package com.flipperdevices.updater.card.model

private const val ALLOWED_BATTERY_CHARGE = 0.09f

sealed class BatteryState {
    data object Unknown : BatteryState()
    class Ready(
        val isCharging: Boolean,
        val batteryLevel: Float
    ) : BatteryState()

    fun isAllowToUpdate(): Boolean {
        return when (this) {
            is Unknown -> false
            is Ready -> isCharging || batteryLevel > ALLOWED_BATTERY_CHARGE
        }
    }
}
