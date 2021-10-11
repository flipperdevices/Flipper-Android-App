package com.flipper.bridge.provider

import com.flipper.bridge.di.DaggerFlipperBleComponent
import com.flipper.bridge.di.FlipperBleComponent

internal object FlipperBleComponentProvider {
    val component: FlipperBleComponent by lazy {
        DaggerFlipperBleComponent.create()
    }
}
