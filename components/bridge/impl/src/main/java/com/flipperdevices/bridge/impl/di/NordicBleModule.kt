package com.flipperdevices.bridge.impl.di

import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat

@Module
@ContributesTo(AppGraph::class)
class NordicBleModule {
    @Provides
    fun provideBluetoothLeScanner(): BluetoothLeScannerCompat {
        return BluetoothLeScannerCompat.getScanner()
    }
}
