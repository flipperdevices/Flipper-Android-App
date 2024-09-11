package com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.internal

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.remotecontrols.api.FlipperDispatchDialogApi
import com.flipperdevices.remotecontrols.api.model.ServerRemoteControlParam
import com.flipperdevices.remotecontrols.grid.remote.api.RemoteGridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.remote.composable.RemoteGridComposable
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.RemoteGridComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.statusbar.ThemeStatusBarIconStyleProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, RemoteGridScreenDecomposeComponent.Factory::class)
class RemoteGridScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted param: ServerRemoteControlParam,
    @Assisted onBack: DecomposeOnBackParameter,
    @Assisted onSaveKey: (NotSavedFlipperKey) -> Unit,
    remoteGridComponentFactory: RemoteGridComponent.Factory,
    flipperDispatchDialogApiFactory: FlipperDispatchDialogApi.Factory,
    private val errorsRenderer: FapHubComposableErrorsRenderer,
    dataStore: DataStore<Settings>
) : RemoteGridScreenDecomposeComponent(componentContext) {
    private val gridComponent = remoteGridComponentFactory.invoke(
        componentContext = childContext("GridComponent"),
        param = param,
        onBack = onBack,
        onSaveKey = onSaveKey
    )
    private val flipperDispatchDialogApi = flipperDispatchDialogApiFactory.invoke(onBack = onBack)
    private val themeStatusBarIconStyleProvider = ThemeStatusBarIconStyleProvider(dataStore)

    @Composable
    override fun Render() {
        RemoteGridComposable(
            remoteGridComponent = gridComponent,
            errorsRenderer = errorsRenderer,
            flipperDispatchDialogApi = flipperDispatchDialogApi
        )
    }

    override fun isStatusBarIconLight(systemIsDark: Boolean): Boolean {
        return themeStatusBarIconStyleProvider.isStatusBarIconLight(systemIsDark)
    }
}
