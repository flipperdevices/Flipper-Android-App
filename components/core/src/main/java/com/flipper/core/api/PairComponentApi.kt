package com.flipper.core.api

import android.content.Context
import com.flipper.core.models.BLEDevice

/**
 * Class which provide api to pair module
 */
interface PairComponentApi {
    /**
     * @return true if we already pass pair screen at least one time
     */
    fun isAtLeastOneTimePaired(): Boolean

    /**
     * @return paired device
     */
    fun getPairedDevice(): BLEDevice

    /**
     * Open screen with pair logic
     */
    fun openPairScreen(context: Context)
}