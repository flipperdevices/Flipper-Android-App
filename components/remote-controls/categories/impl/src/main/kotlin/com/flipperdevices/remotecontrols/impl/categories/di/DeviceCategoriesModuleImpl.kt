package com.flipperdevices.remotecontrols.impl.categories.di

import com.flipperdevices.ifrmvp.api.backend.di.ApiBackendModule
import com.flipperdevices.remotecontrols.api.CategoriesScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.categories.presentation.data.BackendDeviceCategoriesRepository
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.DeviceCategoriesComponent
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.internal.CategoriesScreenDecomposeComponentImpl
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.internal.DeviceCategoriesComponentImpl
import com.flipperdevices.remotecontrols.impl.categories.presentation.viewmodel.DeviceCategoryListViewModel
import kotlinx.coroutines.Dispatchers

class DeviceCategoriesModuleImpl(
    private val apiBackendModule: ApiBackendModule,
) : DeviceCategoriesModule {

    private val deviceCategoriesFactory = let {
        DeviceCategoriesComponent.Factory { componentContext, onBackClicked, onCategoryClicked ->
            val deviceCategoriesRepository = BackendDeviceCategoriesRepository(
                apiBackend = apiBackendModule.apiBackend,
                ioDispatcher = Dispatchers.IO
            )
            DeviceCategoriesComponentImpl(
                componentContext = componentContext,
                onCategoryClicked = onCategoryClicked,
                onBackClicked = onBackClicked,
                createDeviceCategoryListViewModel = {
                    DeviceCategoryListViewModel(
                        deviceCategoriesRepository = deviceCategoriesRepository
                    )
                }
            )
        }
    }

    override val categoriesScreenDecomposeComponentFactory = let {
        CategoriesScreenDecomposeComponent.Factory { componentContext, onBackClicked, onCategoryClicked ->
            CategoriesScreenDecomposeComponentImpl(
                componentContext = componentContext,
                deviceCategoriesComponentFactory = deviceCategoriesFactory,
                onBackClicked = onBackClicked,
                onCategoryClicked = onCategoryClicked
            )
        }
    }
}
