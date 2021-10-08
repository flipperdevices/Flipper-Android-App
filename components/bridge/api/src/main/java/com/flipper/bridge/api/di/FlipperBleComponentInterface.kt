package com.flipper.bridge.api.di

import com.flipper.bridge.api.pair.FlipperPairApi
import com.flipper.bridge.api.scanner.FlipperScanner

interface FlipperBleComponentInterface {
    val flipperScanner: FlipperScanner
    val flipperPairApi: FlipperPairApi
}
