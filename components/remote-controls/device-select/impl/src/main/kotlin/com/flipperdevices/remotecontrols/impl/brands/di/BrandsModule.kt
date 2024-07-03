package com.flipperdevices.remotecontrols.impl.brands.di

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ifrmvp.api.backend.di.ApiBackendModule
import com.flipperdevices.remotecontrols.impl.brands.presentation.data.BackendBrandsRepository
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.BrandsDecomposeComponent
import com.flipperdevices.remotecontrols.api.BrandsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.internal.BrandsScreenDecomposeComponentImpl
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.internal.BrandsDecomposeComponentImpl
import com.flipperdevices.remotecontrols.impl.brands.presentation.viewmodel.BrandsListViewModel
import com.flipperdevices.remotecontrols.impl.brands.presentation.viewmodel.QueryViewModel
import kotlinx.coroutines.Dispatchers

interface BrandsModule {
    val brandsScreenDecomposeComponentFactory: BrandsScreenDecomposeComponent.Factory

    class Default(private val apiBackendModule: ApiBackendModule) : BrandsModule {
        private val brandsDecomposeComponentFactory = object : BrandsDecomposeComponent.Factory {
            override fun createBrandsComponent(
                componentContext: ComponentContext,
                categoryId: Long,
                onBackClicked: () -> Unit,
                onBrandClicked: (brandId: Long) -> Unit
            ): BrandsDecomposeComponent {
                return BrandsDecomposeComponentImpl(
                    componentContext = componentContext,
                    onBackClicked = onBackClicked,
                    createBrandsListViewModel = {
                        BrandsListViewModel(
                            categoryId = categoryId,
                            brandsRepository = BackendBrandsRepository(
                                apiBackend = apiBackendModule.apiBackend,
                                ioDispatcher = Dispatchers.IO
                            )
                        )
                    },
                    createQueryViewModel = {
                        QueryViewModel()
                    },
                    onBrandClicked = onBrandClicked
                )
            }
        }
        override val brandsScreenDecomposeComponentFactory = object : BrandsScreenDecomposeComponent.Factory {
            override fun createBrandsComponent(
                componentContext: ComponentContext,
                categoryId: Long,
                onBackClicked: () -> Unit,
                onBrandClicked: (brandId: Long) -> Unit
            ): BrandsScreenDecomposeComponent {
                return BrandsScreenDecomposeComponentImpl(
                    componentContext = componentContext,
                    brandsDecomposeComponentFactory = brandsDecomposeComponentFactory,
                    categoryId = categoryId,
                    onBackClicked = onBackClicked,
                    onBrandClicked = onBrandClicked
                )
            }
        }
    }
}
