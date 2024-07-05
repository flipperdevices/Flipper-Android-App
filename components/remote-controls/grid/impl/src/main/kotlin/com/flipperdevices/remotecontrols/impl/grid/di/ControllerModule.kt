package com.flipperdevices.remotecontrols.impl.grid.di

import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.ifrmvp.api.backend.di.ApiBackendModule
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.di.SetupModule
import com.flipperdevices.remotecontrols.impl.grid.presentation.data.BackendPagesRepository
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal.GridComponentImpl
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal.GridScreenDecomposeComponentImpl
import com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel.GridViewModel

interface ControllerModule {

    val gridScreenDecomposeComponentFactory: GridScreenDecomposeComponent.Factory

    class Default(
        apiBackendModule: ApiBackendModule,
        serviceProvider: FlipperServiceProvider,
        setupModule: SetupModule
    ) : ControllerModule {
        private val gridComponentFactory =
            GridComponent.Factory { componentContext, param, onPopClicked ->
                GridComponentImpl(
                    componentContext = componentContext,
                    param = param,
                    onPopClicked = onPopClicked,
                    createGridViewModel = { onIrFileLoaded ->
                        GridViewModel(
                            pagesRepository = BackendPagesRepository(
                                apiBackend = apiBackendModule.apiBackend
                            ),
                            param = param,
                            onIrFileLoaded = onIrFileLoaded
                        )
                    },
                    createSaveSignalViewModel = { setupModule.createSaveSignalApi() },
                    createDispatchSignalViewModel = { setupModule.createDispatchSignalApi() }
                )
            }
        override val gridScreenDecomposeComponentFactory =
            GridScreenDecomposeComponent.Factory { componentContext, param, onPopClicked ->
                GridScreenDecomposeComponentImpl(
                    componentContext = componentContext,
                    param = param,
                    gridComponentFactory = gridComponentFactory,
                    onPopClicked = onPopClicked
                )
            }
    }
}
