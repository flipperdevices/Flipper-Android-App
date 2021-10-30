package com.flipperdevices.bridge.impl.di

import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat

@Module
class NordicBleModule {
    @Provides
    @Singleton
    fun provideBluetoothLeScanner(): BluetoothLeScannerCompat {
        return BluetoothLeScannerCompat.getScanner()
    }
}
