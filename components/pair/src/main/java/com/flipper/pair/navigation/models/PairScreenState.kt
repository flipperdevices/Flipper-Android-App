package com.flipper.pair.navigation.models

/**
 * State represent current state of pair process
 */
data class PairScreenState(
    val tosAccepted: Boolean,
    val guidePassed: Boolean,
    val permissionGranted: Boolean,
    val devicePaired: Boolean
)