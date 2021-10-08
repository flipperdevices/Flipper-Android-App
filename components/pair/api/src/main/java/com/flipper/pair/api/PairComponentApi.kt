package com.flipper.pair.api

import android.content.Context

/**
 * Class which provide api to pair module
 */
interface PairComponentApi {
    /**
     * @return true if we already pass pair screen at least one time
     */
    fun shouldWeOpenPairScreen(): Boolean

    /**
     * @return paired device
     */
    fun getPairedDevice(): String

    /**
     * Open screen with pair logic
     */
    fun openPairScreen(context: Context, vararg args: PairScreenArgument)
}
