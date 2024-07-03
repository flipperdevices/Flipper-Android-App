package com.flipperdevices.remotecontrols.impl.setup.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.remotecontrols.impl.categories.composable.components.ErrorComposable
import com.flipperdevices.remotecontrols.impl.categories.composable.components.LoadingComposable
import com.flipperdevices.remotecontrols.impl.categories.composable.components.SharedTopBar
import com.flipperdevices.remotecontrols.impl.setup.composable.components.LoadedContent
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.flipperdevices.remotecontrols.device.select.impl.R as RemoteSetupR

@Composable
internal fun SetupScreen(setupComponent: SetupComponent) {
    val model by setupComponent.model(rememberCoroutineScope()).collectAsState()
    LaunchedEffect(setupComponent.remoteFoundFlow) {
        setupComponent.remoteFoundFlow
            .onEach { setupComponent.onFileFound(it) }
            .launchIn(this)
    }
    Scaffold(
        backgroundColor = LocalPalletV2.current.surface.backgroundMain.body,
        topBar = {
            SharedTopBar(
                title = stringResource(RemoteSetupR.string.setup_title),
                subtitle = stringResource(RemoteSetupR.string.setup_subtitle),
                onBackClicked = setupComponent::onBackClicked
            )
        }
    ) { scaffoldPaddings ->
        Crossfade(model) { model ->
            when (model) {
                SetupComponent.Model.Error -> {
                    ErrorComposable {
                        setupComponent.onSuccessClicked()
                    }
                }

                is SetupComponent.Model.Loaded -> {
                    LoadedContent(
                        model = model,
                        modifier = Modifier.padding(scaffoldPaddings),
                        onPositiveClicked = setupComponent::onSuccessClicked,
                        onNegativeClicked = setupComponent::onFailedClicked,
                        onDispatchSignalClicked = setupComponent::dispatchSignal
                    )
                }

                is SetupComponent.Model.Loading -> {
                    LoadingComposable(progress = model.progress)
                }
            }
        }
    }
}
