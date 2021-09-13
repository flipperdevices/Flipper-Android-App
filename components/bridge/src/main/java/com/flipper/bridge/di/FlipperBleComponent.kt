package com.flipper.bridge.di

import com.flipper.bridge.impl.scanner.FlipperScannerImpl
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NordicBleModule::class])
interface FlipperBleComponent {
    fun inject(flipperScanner: FlipperScannerImpl)
}