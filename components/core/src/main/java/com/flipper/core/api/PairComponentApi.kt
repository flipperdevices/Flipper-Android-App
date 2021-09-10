package com.flipper.core.api

import android.content.Context

/**
 * Class which provide api to pair module
 */
interface PairComponentApi {
    /**
     * @return true if we already pass pair screen at least one time
     */
    fun isAtLeastOneTimePaired(): Boolean

    /**
     * Open screen with pair logic
     */
    fun openPairScreen(context: Context)
}