package com.flipperdevices.firstpair.api

/**
 * Class which provide api to pair module
 */
interface FirstPairApi {
    /**
     * @return true if we already pass pair screen at least one time
     */
    fun shouldWeOpenPairScreen(): Boolean
}
