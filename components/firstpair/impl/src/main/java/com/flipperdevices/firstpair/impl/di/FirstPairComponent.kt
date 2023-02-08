package com.flipperdevices.firstpair.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.firstpair.impl.fragments.permissions.BluetoothEnableHelper
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface FirstPairComponent {
    fun inject(bluetoothEnableHelper: BluetoothEnableHelper)
}
