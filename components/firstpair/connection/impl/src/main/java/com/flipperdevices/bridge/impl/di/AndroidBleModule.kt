package com.flipperdevices.bridge.impl.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.content.ContextCompat
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.getBluetoothAdapter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppGraph::class)
interface AndroidBleModule {
    @Provides
    @SingleIn(AppGraph::class)
    fun provideBluetoothAdapter(bluetoothManager: BluetoothManager?): BluetoothAdapter {
        return bluetoothManager.getBluetoothAdapter()
    }

    @Provides
    @SingleIn(AppGraph::class)
    fun provideBluetoothManager(context: Context): BluetoothManager? {
        return ContextCompat.getSystemService(context, BluetoothManager::class.java)
    }
}
