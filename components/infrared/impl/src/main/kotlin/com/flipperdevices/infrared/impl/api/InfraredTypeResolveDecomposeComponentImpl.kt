package com.flipperdevices.infrared.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.infrared.impl.viewmodel.InfraredTypeViewModel
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class InfraredTypeResolveDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val keyPath: FlipperKeyPath,
    @Assisted private val onCallback: (Callback) -> Unit,
    private val createInfraredTypeViewModel: InfraredTypeViewModel.Factory
) : ScreenDecomposeComponent(componentContext) {

    @Composable
    override fun Render() {
        val createInfraredTypeViewModel = viewModelWithFactory(key = null) {
            createInfraredTypeViewModel.invoke(keyPath)
        }
        LaunchedEffect(createInfraredTypeViewModel) {
            createInfraredTypeViewModel.state
                .onEach {
                    when (it) {
                        InfraredTypeViewModel.State.Default -> {
                            onCallback.invoke(Callback.Default)
                        }

                        InfraredTypeViewModel.State.RemoteControl -> {
                            onCallback.invoke(Callback.RemoteControl)
                        }

                        else -> Unit
                    }
                }.launchIn(this)
            createInfraredTypeViewModel.tryLoad()
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            keyPath: FlipperKeyPath,
            onCallback: (Callback) -> Unit
        ): InfraredTypeResolveDecomposeComponentImpl
    }

    sealed interface Callback {
        data object RemoteControl : Callback
        data object Default : Callback
    }
}
