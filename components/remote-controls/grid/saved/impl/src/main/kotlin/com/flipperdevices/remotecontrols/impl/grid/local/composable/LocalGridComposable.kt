package com.flipperdevices.remotecontrols.impl.grid.local.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.layout.shared.SharedTopBar
import com.flipperdevices.remotecontrols.impl.grid.local.composable.components.ComposableSynchronizationNotification
import com.flipperdevices.remotecontrols.impl.grid.local.composable.components.LocalGridComposableContent
import com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.LocalGridComponent

@Composable
fun LocalGridComposable(
    localGridComponent: LocalGridComponent,
    onError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val model by remember(localGridComponent, coroutineScope) {
        localGridComponent.model(coroutineScope)
    }.collectAsState()
    LaunchedEffect(model) {
        if (model is LocalGridComponent.Model.Error) onError.invoke()
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            SharedTopBar(onBackClick = localGridComponent::pop)
        },
        backgroundColor = LocalPalletV2.current.surface.backgroundMain.body,
        scaffoldState = scaffoldState,
        content = { scaffoldPaddings ->
            LocalGridComposableContent(
                localGridComponent = localGridComponent,
                model = model,
                modifier = Modifier.padding(scaffoldPaddings)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(14.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                val state = (model as? LocalGridComponent.Model.Loaded)
                    ?.synchronizationState
                    ?: SynchronizationState.NotStarted
                ComposableSynchronizationNotification(state)
            }
        }
    )
}
