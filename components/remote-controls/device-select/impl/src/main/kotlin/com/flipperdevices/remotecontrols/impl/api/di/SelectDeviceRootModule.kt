package com.flipperdevices.remotecontrols.impl.api.di

import com.flipperdevices.ifrmvp.api.backend.di.ApiBackendModule
import com.flipperdevices.remotecontrols.impl.grid.di.ControllerModule
import com.flipperdevices.remotecontrols.impl.brands.di.BrandsModule
import com.flipperdevices.remotecontrols.impl.categories.di.DeviceCategoriesModule
import com.flipperdevices.remotecontrols.impl.categories.di.DeviceCategoriesModuleImpl
import com.flipperdevices.remotecontrols.impl.setup.di.SetupModule

interface SelectDeviceRootModule {
    fun createDeviceCategoriesModule(): DeviceCategoriesModule

    fun createBrandsModule(): BrandsModule

    fun createSetupModule(): SetupModule

    fun createGridModule(): ControllerModule

    class Default(
        private val apiBackendModule: ApiBackendModule
    ) : SelectDeviceRootModule {

        override fun createDeviceCategoriesModule(): DeviceCategoriesModule {
            return DeviceCategoriesModuleImpl(
                apiBackendModule = apiBackendModule
            )
        }

        override fun createBrandsModule(): BrandsModule {
            return BrandsModule.Default(
                apiBackendModule = apiBackendModule
            )
        }

        override fun createSetupModule(): SetupModule {
            return SetupModule.Default(apiBackendModule = apiBackendModule)
        }

        override fun createGridModule(): ControllerModule {
            return ControllerModule.Default(apiBackendModule = apiBackendModule)
        }
    }
}
