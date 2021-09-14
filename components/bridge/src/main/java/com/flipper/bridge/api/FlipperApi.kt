package com.flipper.bridge.api

import com.flipper.bridge.di.FlipperBleComponentInterface
import com.flipper.bridge.di.FlipperBleComponentProvider

/**
 * Entry point for Flipper Api
 */
object FlipperApi : FlipperBleComponentInterface by FlipperBleComponentProvider.component
