package com.flipperdevices.faphub.installation.button.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.impl.composable.states.ComposableFapButton
import com.flipperdevices.faphub.installation.button.impl.viewmodel.FapStatusViewModel
import com.flipperdevices.faphub.installation.button.impl.viewmodel.OpenFapViewModel
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class, FapInstallationUIApi::class)
class FapInstallationUIApiImpl @Inject constructor(
    private val openFapViewModelProvider: Provider<OpenFapViewModel>,
    private val fapStatusViewModelProvider: Provider<FapStatusViewModel>
) : FapInstallationUIApi {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun ComposableButton(
        modifier: Modifier,
        componentContext: ComponentContext,
        config: FapButtonConfig?,
        fapButtonSize: FapButtonSize
    ) {
        val statusViewModel = componentContext.viewModelWithFactory(key = null) {
            fapStatusViewModelProvider.get()
        }
        val stateFlow = remember(config) {
            statusViewModel.getStateForApplicationId(config)
        }
        val state by stateFlow.collectAsState(FapState.NotInitialized)
        val openViewModel = componentContext.viewModelWithFactory(key = null) {
            openFapViewModelProvider.get()
        }

        ComposableFapButton(
            modifier = modifier,
            localState = state,
            fapButtonSize = fapButtonSize,
            statusViewModel = statusViewModel,
            config = config,
            openViewModel = openViewModel
        )
    }
}
