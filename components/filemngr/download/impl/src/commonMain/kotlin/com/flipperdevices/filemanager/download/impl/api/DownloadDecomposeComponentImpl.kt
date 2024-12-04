package com.flipperdevices.filemanager.download.impl.api

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.filemanager.download.api.DownloadDecomposeComponent
import com.flipperdevices.filemanager.download.impl.composable.DownloadingComposable
import com.flipperdevices.filemanager.download.impl.viewmodel.DownloadViewModel
import com.flipperdevices.filemanager.download.model.DownloadableFile
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, DownloadDecomposeComponent.Factory::class)
class DownloadDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val downloadViewModelFactory: Provider<DownloadViewModel>,
) : DownloadDecomposeComponent(componentContext) {
    private val downloadViewModel = instanceKeeper.getOrCreate {
        downloadViewModelFactory.get()
    }

    override val isInProgress = downloadViewModel.state
        .map { state -> state is DownloadViewModel.State.Downloading }
        .stateIn(coroutineScope(), SharingStarted.Eagerly, false)

    override fun onCancel() = downloadViewModel.onCancel()

    override fun download(file: DownloadableFile) {
        downloadViewModel.tryDownload(file = file)
    }

    @Composable
    override fun Render() {
        val state by downloadViewModel.state.collectAsState()
        when (val localState = state) {
            is DownloadViewModel.State.Downloading -> {
                DownloadingComposable(
                    state = localState,
                    onCancel = downloadViewModel::onCancel,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = false, onClick = {})
                        .background(LocalPalletV2.current.surface.backgroundMain.body)
                        .navigationBarsPadding()
                        .systemBarsPadding(),
                )
            }

            DownloadViewModel.State.Error,
            DownloadViewModel.State.Pending,
            DownloadViewModel.State.NotSupported -> Unit
        }
    }
}
