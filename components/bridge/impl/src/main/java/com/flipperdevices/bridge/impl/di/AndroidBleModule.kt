package com.flipperdevices.bridge.impl.di

import android.bluetooth.BluetoothAdapter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AndroidBleModule {
    @Provides
    @Singleton
    fun provideBluetoothAdapter(): BluetoothAdapter {
        return BluetoothAdapter.getDefaultAdapter()
    }
}
