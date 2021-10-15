package com.flipperdevices.bridge.provider

import com.flipperdevices.bridge.impl.di.DaggerFlipperBleComponent
import com.flipperdevices.bridge.impl.di.FlipperBleComponent

internal object FlipperBleComponentProvider {
    val component: FlipperBleComponent by lazy {
        DaggerFlipperBleComponent.create()
    }
}
