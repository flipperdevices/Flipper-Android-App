package com.flipperdevices.remotecontrols.impl.setup.presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ifrmvp.backend.model.IfrFileModel
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
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
            val isEmulating: Boolean = false,
            val isEmulated: Boolean
        ) : Model

        data object Error : Model
    }

    interface Factory {
        fun createSetupComponent(
            componentContext: ComponentContext,
            param: SetupScreenDecomposeComponent.Param,
            onBack: DecomposeOnBackParameter,
            onIfrFileFound: (ifrFileId: Long) -> Unit
        ): SetupComponent
    }
}
