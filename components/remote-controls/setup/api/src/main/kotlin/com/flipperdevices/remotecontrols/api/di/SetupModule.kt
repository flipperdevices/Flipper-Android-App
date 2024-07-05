package com.flipperdevices.remotecontrols.api.di

import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.api.SaveSignalApi
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent

interface SetupModule {
    val setupScreenDecomposeComponentFactory: SetupScreenDecomposeComponent.Factory
    fun createSaveSignalApi(): SaveSignalApi
    fun createDispatchSignalApi(): DispatchSignalApi
}
