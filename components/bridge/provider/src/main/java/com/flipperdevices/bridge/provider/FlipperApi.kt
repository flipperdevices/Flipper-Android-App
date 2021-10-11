package com.flipperdevices.bridge.provider

import com.flipperdevices.bridge.api.di.FlipperBleComponentInterface

/**
 * Entry point for Flipper Api
 */
object FlipperApi : FlipperBleComponentInterface by FlipperBleComponentProvider.component
