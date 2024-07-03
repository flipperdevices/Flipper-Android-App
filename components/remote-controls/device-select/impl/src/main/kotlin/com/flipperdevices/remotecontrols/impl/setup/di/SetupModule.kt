package com.flipperdevices.remotecontrols.impl.setup.di

import android.content.Context
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.ifrmvp.api.backend.di.ApiBackendModule
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal.SetupComponentImpl
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal.SetupScreenDecomposeComponentImpl
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.CurrentSignalViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.HistoryViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.SaveSignalViewModel

interface SetupModule {
    val setupScreenDecomposeComponentFactory: SetupScreenDecomposeComponent.Factory

    class Default(
        apiBackendModule: ApiBackendModule,
        serviceProvider: FlipperServiceProvider,
        context: Context
    ) : SetupModule {
        private val setupComponentFactory = object : SetupComponent.Factory {
            override fun createSetupComponent(
                componentContext: ComponentContext,
                param: SetupScreenDecomposeComponent.Param,
                onBack: () -> Unit,
                onIfrFileFound: (ifrFileId: Long) -> Unit
            ): SetupComponent {
                return SetupComponentImpl(
                    componentContext = componentContext,
                    param = param,
                    onBackClicked = onBack,
                    onIfrFileFound = onIfrFileFound,
                    createHistoryViewModel = {
                        HistoryViewModel()
                    },
                    createCurrentSignalViewModel = { onLoaded ->
                        CurrentSignalViewModel(
                            param = param,
                            apiBackend = apiBackendModule.apiBackend,
                            onLoaded = onLoaded
                        )
                    },
                    createSaveSignalViewModel = {
                        SaveSignalViewModel(
                            context = context,
                            serviceProvider = serviceProvider
                        )
                    }
                )
            }
        }
        override val setupScreenDecomposeComponentFactory =
            object : SetupScreenDecomposeComponent.Factory {
                override fun createSetupComponent(
                    componentContext: ComponentContext,
                    param: SetupScreenDecomposeComponent.Param,
                    onBack: () -> Unit,
                    onIfrFileFound: (ifrFileId: Long) -> Unit
                ): SetupScreenDecomposeComponent {
                    return SetupScreenDecomposeComponentImpl(
                        componentContext = componentContext,
                        setupComponentFactory = setupComponentFactory,
                        param = param,
                        onBack = onBack,
                        onIfrFileFound = onIfrFileFound
                    )
                }
            }
    }
}
