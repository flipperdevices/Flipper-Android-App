package com.flipperdevices.firstpair.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.firstpair.impl.fragments.permissions.BluetoothEnableHelper
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppGraph::class)
interface FirstPairComponent {
    fun inject(bluetoothEnableHelper: BluetoothEnableHelper)
}
