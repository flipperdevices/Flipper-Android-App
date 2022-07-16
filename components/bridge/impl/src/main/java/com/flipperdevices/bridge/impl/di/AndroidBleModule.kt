package com.flipperdevices.bridge.impl.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.content.ContextCompat
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
@ContributesTo(AppGraph::class)
class AndroidBleModule {
    @Provides
    @Singleton
    fun provideBluetoothAdapter(bluetoothManager: BluetoothManager?): BluetoothAdapter {
        return bluetoothManager?.adapter ?: BluetoothAdapter.getDefaultAdapter()
    }

    @Provides
    @Singleton
    fun provideBluetoothManager(context: Context): BluetoothManager? {
        return ContextCompat.getSystemService(context, BluetoothManager::class.java)
    }
}
