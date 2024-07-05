package com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.flipperdevices.infrared.editor.model.InfraredRemote
import com.flipperdevices.infrared.editor.viewmodel.InfraredKeyParser
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.data.PagesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GridViewModel @AssistedInject constructor(
    private val pagesRepository: PagesRepository,
    @Assisted private val param: GridScreenDecomposeComponent.Param,
    @Assisted private val onIrFileLoaded: (String) -> Unit
) : DecomposeViewModel() {
    val state = MutableStateFlow<State>(State.Loading)

    private suspend fun loadIrFileContent(): List<InfraredRemote> {
        return pagesRepository.fetchKeyContent(param.ifrFileId)
            .onFailure { state.value = State.Error }
            .onSuccess(onIrFileLoaded)
            .map(FlipperFileFormat::fromFileContent)
            .map(InfraredKeyParser::mapParsedKeyToInfraredRemotes)
            .getOrNull()
            .orEmpty()
    }

    fun tryLoad() {
        viewModelScope.launch {
            val remotes = loadIrFileContent()
            val pagesLayoutResult = pagesRepository.fetchDefaultPageLayout(
                ifrFileId = param.ifrFileId,
            )
            pagesLayoutResult
                .onFailure { state.value = State.Error }
                .onSuccess { pagesLayout ->
                    state.value = State.Loaded(
                        pagesLayout = pagesLayout,
                        remotes = remotes
                    )
                }
        }
    }

    init {
        tryLoad()
    }

    sealed interface State {
        data object Loading : State
        data object Error : State
        data class Loaded(
            val pagesLayout: PagesLayout,
            val remotes: List<InfraredRemote>
        ) : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            param: GridScreenDecomposeComponent.Param,
            onIrFileLoaded: (String) -> Unit
        ): GridViewModel
    }
}
