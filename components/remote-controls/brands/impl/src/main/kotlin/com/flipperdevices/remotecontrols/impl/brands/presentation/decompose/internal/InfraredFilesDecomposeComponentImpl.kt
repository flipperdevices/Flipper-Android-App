package com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.remotecontrols.api.InfraredsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.brands.composable.InfraredsScreen
import com.flipperdevices.remotecontrols.impl.brands.presentation.viewmodel.InfraredsListViewModel
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.model.RootScreenConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, InfraredsScreenDecomposeComponent.Factory::class)
class InfraredFilesDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val brandId: Long,
    @Assisted private val onBackClick: () -> Unit,
    private val infraredsListViewModelFactory: InfraredsListViewModel.Factory,
) : InfraredsScreenDecomposeComponent(componentContext) {

    @Composable
    override fun Render() {
        val viewModel = viewModelWithFactory(null) {
            infraredsListViewModelFactory.invoke(brandId)
        }
        val navigation = LocalRootNavigation.current
        InfraredsScreen(
            viewModel = viewModel,
            onBack = onBackClick,
            onReload = viewModel::tryLoad,
            onClick = {
                navigation.push(RootScreenConfig.ServerRemoteControl(it.id, it.folderName))
            },
        )
    }
}
