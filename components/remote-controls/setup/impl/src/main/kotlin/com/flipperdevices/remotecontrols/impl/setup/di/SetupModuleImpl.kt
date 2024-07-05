package com.flipperdevices.remotecontrols.impl.setup.di

import android.content.Context
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.ifrmvp.api.backend.di.ApiBackendModule
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.api.SaveSignalApi
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.di.SetupModule
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal.SetupComponentImpl
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal.SetupScreenDecomposeComponentImpl
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.CurrentSignalViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.DispatchSignalViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.HistoryViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.SaveSignalViewModel

class SetupModuleImpl(
    apiBackendModule: ApiBackendModule,
    private val serviceProvider: FlipperServiceProvider,
    private val context: Context,
    private val emulateHelper: EmulateHelper
) : SetupModule {
    override fun createDispatchSignalApi(): DispatchSignalApi {
        return DispatchSignalViewModel(
            emulateHelper = emulateHelper,
            serviceProvider = serviceProvider
        )
    }

    override fun createSaveSignalApi(): SaveSignalApi {
        return SaveSignalViewModel(
            context = context,
            serviceProvider = serviceProvider
        )
    }

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
                createSaveSignalApi = { createSaveSignalApi() },
                createDispatchSignalApi = { createDispatchSignalApi() }
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
