package com.flipperdevices.bridge.api.di

import com.flipperdevices.bridge.api.pair.FlipperPairApi
import com.flipperdevices.bridge.api.scanner.FlipperScanner

interface FlipperBleComponentInterface {
    val flipperScanner: FlipperScanner
    val flipperPairApi: FlipperPairApi
}
