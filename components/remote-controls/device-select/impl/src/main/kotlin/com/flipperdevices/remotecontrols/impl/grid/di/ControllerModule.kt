package com.flipperdevices.remotecontrols.impl.grid.di

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ifrmvp.api.backend.di.ApiBackendModule
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.data.BackendPagesRepository
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal.GridComponentImpl
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal.GridScreenDecomposeComponentImpl
import com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel.GridViewModel

interface ControllerModule {

    val gridComponentFactory: GridScreenDecomposeComponent.Factory

    class Default(apiBackendModule: ApiBackendModule) : ControllerModule {
        private val _gridComponentFactory = object : GridComponent.Factory {
            override fun create(
                componentContext: ComponentContext,
                param: GridScreenDecomposeComponent.Param,
                onPopClicked: () -> Unit
            ): GridComponent {
                return GridComponentImpl(
                    componentContext = componentContext,
                    param = param,
                    onPopClicked = onPopClicked,
                    createGridViewModel = {
                        GridViewModel(
                            pagesRepository = BackendPagesRepository(
                                apiBackend = apiBackendModule.apiBackend
                            ),
                            param = param
                        )
                    }
                )
            }

        }
        override val gridComponentFactory = GridScreenDecomposeComponent.Factory { componentContext, param, onPopClicked ->
            GridScreenDecomposeComponentImpl(
                componentContext = componentContext,
                param = param,
                gridComponentFactory = _gridComponentFactory,
                onPopClicked = onPopClicked
            )
        }
    }
}
