package com.flipperdevices.bridge.provider

import com.flipperdevices.bridge.di.DaggerFlipperBleComponent
import com.flipperdevices.bridge.di.FlipperBleComponent

internal object FlipperBleComponentProvider {
    val component: FlipperBleComponent by lazy {
        DaggerFlipperBleComponent.create()
    }
}
