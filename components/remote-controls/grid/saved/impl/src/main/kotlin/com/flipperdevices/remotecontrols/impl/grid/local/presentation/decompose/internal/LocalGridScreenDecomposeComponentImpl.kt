package com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.remotecontrols.api.FlipperDispatchDialogApi
import com.flipperdevices.remotecontrols.impl.grid.local.api.LocalGridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.local.composable.LocalGridComposable
import com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.LocalGridComponent
import com.flipperdevices.share.api.ShareBottomUIApi
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.gulya.anvil.assisted.ContributesAssistedFactory

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, LocalGridScreenDecomposeComponent.Factory::class)
class LocalGridScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val keyPath: FlipperKeyPath,
    @Assisted onBack: DecomposeOnBackParameter,
    @Assisted private val onCallback: (Callback) -> Unit,
    localGridComponentFactory: LocalGridComponent.Factory,
    private val shareBottomUiApi: ShareBottomUIApi,
    flipperDispatchDialogApiFactory: FlipperDispatchDialogApi.Factory,
) : LocalGridScreenDecomposeComponent(componentContext) {
    private val localGridComponent = localGridComponentFactory.invoke(
        componentContext = childContext("GridComponent_local"),
        keyPath = keyPath,
        onBack = onBack,
    )
    private val flipperDispatchDialogApi = flipperDispatchDialogApiFactory.invoke(onBack = onBack)

    private val isBackPressHandledFlow = MutableStateFlow(false)
    private val backCallback = BackCallback(false) { isBackPressHandledFlow.update { true } }

    init {
        backHandler.register(backCallback)
    }

    @Composable
    override fun Render() {
        shareBottomUiApi.ComposableShareBottomSheet(
            provideFlipperKeyPath = { keyPath },
            onSheetStateVisible = { isShown, onClose ->
                val isBackPressHandled by isBackPressHandledFlow.collectAsState()
                backCallback.isEnabled = isShown

                LaunchedEffect(isBackPressHandled) {
                    if (isBackPressHandled) {
                        onClose()
                        isBackPressHandledFlow.emit(false)
                    }
                }
            },
            componentContext = this
        ) { onShare ->
            LocalGridComposable(
                localGridComponent = localGridComponent,
                flipperDispatchDialogApi = flipperDispatchDialogApi,
                onCallback = onCallback,
                onShare = onShare
            )
        }
    }
}
