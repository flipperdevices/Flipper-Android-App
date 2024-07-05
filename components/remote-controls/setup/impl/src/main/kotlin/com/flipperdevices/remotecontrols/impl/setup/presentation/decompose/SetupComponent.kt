package com.flipperdevices.remotecontrols.impl.setup.presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ifrmvp.backend.model.IfrFileModel
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SetupComponent {
    fun model(coroutineScope: CoroutineScope): StateFlow<Model>
    val remoteFoundFlow: Flow<IfrFileModel>
    val param: SetupScreenDecomposeComponent.Param

    fun onBackClicked()

    fun onSuccessClicked()
    fun onFailedClicked()
    fun dispatchSignal()

    fun tryLoad()

    fun onFileFound(ifrFileModel: IfrFileModel)

    sealed interface Model {
        data class Loading(val progress: Float) : Model
        data class Loaded(val response: SignalResponseModel) : Model

        data object Error : Model
    }

    interface Factory {
        fun createSetupComponent(
            componentContext: ComponentContext,
            param: SetupScreenDecomposeComponent.Param,
            onBack: () -> Unit,
            onIfrFileFound: (ifrFileId: Long) -> Unit
        ): SetupComponent
    }
}
