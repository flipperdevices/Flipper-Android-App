package com.flipperdevices.remotecontrols.impl.setup.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.ifrmvp.core.ui.layout.shared.SharedTopBar
import com.flipperdevices.remotecontrols.api.FlipperDispatchDialogApi
import com.flipperdevices.remotecontrols.impl.setup.composable.components.AnimatedConfirmContent
import com.flipperdevices.remotecontrols.impl.setup.composable.components.LoadedContent
import com.flipperdevices.remotecontrols.impl.setup.composable.components.SetupLoadingContent
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.flipperdevices.remotecontrols.setup.impl.R as SetupR

private val SetupComponent.Model.key: Any
    get() = when (this) {
        is SetupComponent.Model.Error -> "error"
        is SetupComponent.Model.Loaded -> "loaded"
        is SetupComponent.Model.Loading -> "loading"
    }

@Suppress("LongMethod")
@Composable
fun SetupScreen(
    setupComponent: SetupComponent,
    errorsRenderer: FapHubComposableErrorsRenderer,
    flipperDispatchDialogApi: FlipperDispatchDialogApi,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val model by remember(setupComponent, coroutineScope) {
        setupComponent.model(coroutineScope)
    }.collectAsState()
    val lastEmulatedSignal by setupComponent.lastEmulatedSignal.collectAsState()
    LaunchedEffect(setupComponent.remoteFoundFlow) {
        setupComponent.remoteFoundFlow.onEach {
            setupComponent.onFileFound(it)
        }.launchIn(this)
    }
    Scaffold(
        modifier = modifier,
        backgroundColor = LocalPalletV2.current.surface.backgroundMain.body,
        topBar = {
            SharedTopBar(
                title = stringResource(SetupR.string.setup_title),
                subtitle = stringResource(SetupR.string.rcs_step_3),
                onBackClick = setupComponent::onBackClick
            )
        }
    ) { scaffoldPaddings ->
        AnimatedContent(
            targetState = model,
            modifier = Modifier.padding(scaffoldPaddings),
            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
            contentKey = { it.key }
        ) { model ->
            when (model) {
                is SetupComponent.Model.Error -> {
                    errorsRenderer.ComposableThrowableError(
                        throwable = model.throwable,
                        onRetry = setupComponent::tryLoad,
                        fapErrorSize = FapErrorSize.FULLSCREEN,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is SetupComponent.Model.Loaded -> {
                    flipperDispatchDialogApi.Render(
                        dialogType = model.flipperDialog,
                        onDismiss = setupComponent::dismissDialog
                    )
                    LoadedContent(
                        model = model,
                        modifier = Modifier.padding(scaffoldPaddings),
                        onDispatchSignalClick = setupComponent::dispatchSignal,
                        onSkipClick = setupComponent::onSkipClicked,
                    )
                }

                is SetupComponent.Model.Loading -> {
                    SetupLoadingContent()
                }
            }
        }
        AnimatedConfirmContent(
            lastEmulatedSignal = lastEmulatedSignal,
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPaddings),
            onNegativeClick = setupComponent::onFailedClick,
            onSuccessClick = setupComponent::onSuccessClick,
            onSkipClick = setupComponent::onSkipClicked,
            onDismissConfirm = setupComponent::forgetLastEmulatedSignal
        )
    }
}
