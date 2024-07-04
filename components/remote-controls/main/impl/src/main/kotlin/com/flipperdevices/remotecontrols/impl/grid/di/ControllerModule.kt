package com.flipperdevices.remotecontrols.impl.grid.di

import android.content.Context
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.ifrmvp.api.backend.di.ApiBackendModule
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.data.BackendPagesRepository
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal.GridComponentImpl
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal.GridScreenDecomposeComponentImpl
import com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel.GridViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.DispatchSignalViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.SaveSignalViewModel

interface ControllerModule {

    val gridComponentFactory: GridScreenDecomposeComponent.Factory

    class Default(
        apiBackendModule: ApiBackendModule,
        serviceProvider: FlipperServiceProvider,
        context: Context,
        emulateHelper: EmulateHelper
    ) : ControllerModule {
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
                    createGridViewModel = { onIrFileLoaded ->
                        GridViewModel(
                            pagesRepository = BackendPagesRepository(
                                apiBackend = apiBackendModule.apiBackend
                            ),
                            param = param,
                            onIrFileLoaded = onIrFileLoaded
                        )
                    },
                    createSaveSignalViewModel = {
                        SaveSignalViewModel(
                            context = context,
                            serviceProvider = serviceProvider
                        )
                    },
                    createDispatchSignalViewModel = {
                        DispatchSignalViewModel(
                            emulateHelper = emulateHelper,
                            serviceProvider = serviceProvider
                        )
                    }
                )
            }
        }
        override val gridComponentFactory =
            GridScreenDecomposeComponent.Factory { componentContext, param, onPopClicked ->
                GridScreenDecomposeComponentImpl(
                    componentContext = componentContext,
                    param = param,
                    gridComponentFactory = _gridComponentFactory,
                    onPopClicked = onPopClicked
                )
            }
    }
}
