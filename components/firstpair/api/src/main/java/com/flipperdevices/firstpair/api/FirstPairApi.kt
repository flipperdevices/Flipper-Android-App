package com.flipperdevices.firstpair.api

import com.github.terrakok.cicerone.Screen

/**
 * Class which provide api to pair module
 */
interface FirstPairApi {
    fun getFirstPairScreen(): Screen

    /**
     * @return true if we already pass pair screen at least one time
     */
    fun shouldWeOpenPairScreen(): Boolean
}
