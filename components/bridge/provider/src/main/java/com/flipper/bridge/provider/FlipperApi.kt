package com.flipper.bridge.provider

import com.flipper.bridge.api.di.FlipperBleComponentInterface

/**
 * Entry point for Flipper Api
 */
object FlipperApi : FlipperBleComponentInterface by FlipperBleComponentProvider.component
