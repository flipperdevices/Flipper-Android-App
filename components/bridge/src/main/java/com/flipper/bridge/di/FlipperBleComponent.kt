package com.flipper.bridge.di

import com.flipper.bridge.api.pair.FlipperPairApi
import com.flipper.bridge.api.scanner.FlipperScanner
import dagger.Component
import javax.inject.Singleton

interface FlipperBleComponentInterface {
    val flipperScanner: FlipperScanner
    val flipperPairApi: FlipperPairApi
}

@Singleton
@Component(modules = [NordicBleModule::class, FlipperBleModule::class, AndroidBleModule::class])
interface FlipperBleComponent : FlipperBleComponentInterface
