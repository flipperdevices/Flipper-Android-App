package com.flipperdevices.bridge.impl.di

import com.flipperdevices.bridge.api.scanner.FlipperScanner
import com.flipperdevices.bridge.impl.scanner.FlipperScannerImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class FlipperBleModule {
    @Binds
    @Singleton
    abstract fun provideFlipperScanner(flipperScanner: FlipperScannerImpl): FlipperScanner
}
