package com.flipperdevices.remotecontrols.impl.setup.presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ifrmvp.backend.model.IfrFileModel
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.infrared.api.InfraredConnectionApi.InfraredEmulateState
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SetupComponent {
    fun model(coroutineScope: CoroutineScope): StateFlow<Model>

    val remoteFoundFlow: Flow<IfrFileModel>
    val param: SetupScreenDecomposeComponent.Param

    fun onBackClick()

    fun onSuccessClick()
    fun onFailedClick()
    fun dispatchSignal()

    fun dismissBusyDialog()

    fun tryLoad()

    fun onFileFound(ifrFileModel: IfrFileModel)

    sealed interface Model {
        data class Loading(val progress: Float) : Model
        data class Loaded(
            val response: SignalResponseModel,
            val isFlipperBusy: Boolean = false,
            val emulatedKeyIdentifier: IfrKeyIdentifier?,
            val isEmulated: Boolean,
            val connectionState: InfraredEmulateState
        ) : Model {
            val isSyncing: Boolean = listOf(
                InfraredEmulateState.CONNECTING,
                InfraredEmulateState.SYNCING
            ).contains(connectionState)
            val isConnected = connectionState != InfraredEmulateState.NOT_CONNECTED
        }

        data object Error : Model
    }

    interface Factory {
        fun createSetupComponent(
            componentContext: ComponentContext,
            param: SetupScreenDecomposeComponent.Param,
            onBack: DecomposeOnBackParameter,
            onIrFileReady: (id: Long) -> Unit
        ): SetupComponent
    }
}
