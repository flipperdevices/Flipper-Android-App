package com.flipper.bridge.di

import dagger.Module
import dagger.Provides
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import javax.inject.Singleton

@Module
class NordicBleModule {
    @Provides
    @Singleton
    fun provideBluetoothLeScanner(): BluetoothLeScannerCompat {
        return BluetoothLeScannerCompat.getScanner()
    }
}