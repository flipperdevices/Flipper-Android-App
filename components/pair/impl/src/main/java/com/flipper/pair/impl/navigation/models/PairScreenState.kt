package com.flipper.pair.impl.navigation.models

/**
 * State represent current state of pair process
 */
data class PairScreenState(
    val tosAccepted: Boolean,
    val guidePassed: Boolean,
    val permissionGranted: Boolean,
    val devicePaired: Boolean
) {
    fun isAllTrue() = tosAccepted and guidePassed and permissionGranted and devicePaired
}
