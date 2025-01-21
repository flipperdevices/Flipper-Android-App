package com.flipperdevices.bridge.api.manager.ktx.state

/**
 * Copy from https://github.com/NordicSemiconductor/Android-BLE-Library/blob/8fc0fcddba/ble-ktx/src/main/java/no/nordicsemi/android/ble/ktx/state/BondState.kt
 */

sealed class BondState {

    /** The device was not connected or is not bonded. */
    object NotBonded : BondState()

    /** Bonding has started. */
    object Bonding : BondState()

    /** The device is bonded. */
    object Bonded : BondState()

    /** Whether bonding was established. */
    val isBonded: Boolean
        get() = this is Bonded
}
