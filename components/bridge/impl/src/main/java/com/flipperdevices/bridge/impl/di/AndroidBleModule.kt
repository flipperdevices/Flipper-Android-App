package com.flipperdevices.bridge.impl.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.content.ContextCompat
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

@Module
@ContributesTo(AppGraph::class)
class AndroidBleModule {
    @Provides
    fun provideBluetoothAdapter(context: Context): BluetoothAdapter {
        val bluetoothManager = ContextCompat.getSystemService(context, BluetoothManager::class.java)

        return bluetoothManager?.adapter ?: BluetoothAdapter.getDefaultAdapter()
    }
}
