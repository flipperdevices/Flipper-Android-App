package com.flipperdevices.faphub.installation.button.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.impl.composable.states.ComposableFapButton
import com.flipperdevices.faphub.installation.button.impl.viewmodel.FapStatusViewModel
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.squareup.anvil.annotations.ContributesBinding
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapInstallationUIApi::class)
class FapInstallationUIApiImpl @Inject constructor(
    private val bottomBarApi: BottomNavigationHandleDeeplink
) : FapInstallationUIApi {
    @Composable
    override fun ComposableButton(
        config: FapButtonConfig?,
        modifier: Modifier,
        fapButtonSize: FapButtonSize
    ) {
        val statusViewModel = tangleViewModel<FapStatusViewModel>()
        val stateFlow = remember(config) {
            statusViewModel.getStateForApplicationId(config)
        }
        val state by stateFlow.collectAsState(FapState.NotInitialized)

        ComposableFapButton(
            modifier = modifier,
            localState = state,
            fapButtonSize = fapButtonSize,
            statusViewModel = statusViewModel,
            config = config,
            bottomBarApi = bottomBarApi
        )
    }
}
