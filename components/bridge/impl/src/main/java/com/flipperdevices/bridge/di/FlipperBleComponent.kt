package com.flipperdevices.bridge.di

import com.flipperdevices.bridge.api.di.FlipperBleComponentInterface
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NordicBleModule::class, FlipperBleModule::class, AndroidBleModule::class])
interface FlipperBleComponent : FlipperBleComponentInterface
