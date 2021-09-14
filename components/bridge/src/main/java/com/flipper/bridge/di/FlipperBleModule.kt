package com.flipper.bridge.di

import com.flipper.bridge.api.pair.FlipperPairApi
import com.flipper.bridge.api.scanner.FlipperScanner
import com.flipper.bridge.impl.pair.FlipperPairApiImpl
import com.flipper.bridge.impl.scanner.FlipperScannerImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class FlipperBleModule {
    @Binds
    @Singleton
    abstract fun provideFlipperScanner(flipperScanner: FlipperScannerImpl): FlipperScanner

    @Binds
    @Singleton
    abstract fun provideFlipperPairApi(flipperPairApi: FlipperPairApiImpl): FlipperPairApi
}