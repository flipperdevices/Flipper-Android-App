package com.flipper.bridge.di

import com.flipper.bridge.api.di.FlipperBleComponentInterface
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NordicBleModule::class, FlipperBleModule::class, AndroidBleModule::class])
interface FlipperBleComponent : FlipperBleComponentInterface
