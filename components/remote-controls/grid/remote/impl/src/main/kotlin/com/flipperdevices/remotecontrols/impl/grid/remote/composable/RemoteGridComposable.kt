package com.flipperdevices.remotecontrols.impl.grid.remote.composable

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.remotecontrols.api.FlipperDispatchDialogApi
import com.flipperdevices.remotecontrols.impl.grid.remote.composable.components.RemoteGridComposableContent
import com.flipperdevices.remotecontrols.impl.grid.remote.composable.components.RemoteGridTopBar
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.RemoteGridComponent

@Composable
fun RemoteGridComposable(
    remoteGridComponent: RemoteGridComponent,
    flipperDispatchDialogApi: FlipperDispatchDialogApi,
    errorsRenderer: FapHubComposableErrorsRenderer,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val model by remember(remoteGridComponent, coroutineScope) {
        remoteGridComponent.model(coroutineScope)
    }.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            RemoteGridTopBar(
                isFilesSaved = model.isFilesSaved,
                onBack = remoteGridComponent::pop,
                remoteName = (model as? RemoteGridComponent.Model.Loaded)?.let {
                    remoteGridComponent.param.remoteName
                },
                onSave = remoteGridComponent::save,
                saveProgress = (model as? RemoteGridComponent.Model.Loaded)?.saveProgressOrNull
            )
        },
        backgroundColor = LocalPalletV2.current.surface.backgroundMain.body,
        scaffoldState = scaffoldState,
        content = { scaffoldPaddings ->
            RemoteGridComposableContent(
                remoteGridComponent = remoteGridComponent,
                flipperDispatchDialogApi = flipperDispatchDialogApi,
                model = model,
                modifier = Modifier
                    .padding(scaffoldPaddings)
                    .navigationBarsPadding(),
                errorsRenderer = errorsRenderer
            )
        }
    )
}
