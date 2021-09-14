package com.flipper.bridge.di

internal object FlipperBleComponentProvider {
    val component: FlipperBleComponent by lazy {
        DaggerFlipperBleComponent.create()
    }
}
