package com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import com.flipperdevices.infrared.editor.core.parser.InfraredKeyParser
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.data.localpages.LocalPagesRepository
import com.flipperdevices.remotecontrols.impl.grid.presentation.data.pages.PagesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
class GridViewModel @AssistedInject constructor(
    private val pagesRepository: PagesRepository,
    private val localPagesRepository: LocalPagesRepository,
    @Assisted private val param: GridScreenDecomposeComponent.Param,
    @Assisted private val onIrFileLoaded: (String) -> Unit,
    @Assisted private val onUiLoaded: (PagesLayout) -> Unit
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "GridViewModel"

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    fun tryLoad() {
        viewModelScope.launch {
            val localPagesLayout = localPagesRepository.getLocalPagesLayout(param.ifrFileId)
            val localRemotesRaw = localPagesRepository.getLocalFlipperKey(param.ifrFileId)
                ?.keyContent
                ?.openStream()
                ?.reader()
                ?.readText()
            val pagesLayout = localPagesLayout
                ?: pagesRepository.fetchDefaultPageLayout(ifrFileId = param.ifrFileId)
                    .onSuccess(onUiLoaded)
                    .onFailure { _state.emit(State.Error) }
                    .onFailure { throwable -> error(throwable) { "#tryLoad could not load ui model" } }
                    .getOrNull() ?: return@launch

            val remotesRaw = localRemotesRaw
                ?: pagesRepository.fetchKeyContent(param.ifrFileId)
                    .onFailure { _state.emit(State.Error) }
                    .onFailure { throwable -> error(throwable) { "#tryLoad could not load key content" } }
                    .onSuccess(onIrFileLoaded)
                    .getOrNull()
                    .orEmpty()
            _state.emit(
                value = State.Loaded(
                    pagesLayout = pagesLayout,
                    remotes = remotesRaw
                        .let(FlipperFileFormat::fromFileContent)
                        .let(InfraredKeyParser::mapParsedKeyToInfraredRemotes)
                        .toImmutableList(),
                    remotesRaw = remotesRaw,
                    isDownloaded = localPagesLayout != null && localRemotesRaw != null
                )
            )
        }
    }

    fun delete() {
        val state = state.value as? State.Loaded ?: return
        viewModelScope.launch {
            localPagesRepository.delete(param.ifrFileId)
            _state.emit(state.copy(isDownloaded = false))
        }
    }

    fun saveSignal() {
        val state = state.value as? State.Loaded ?: return
        viewModelScope.launch {
            localPagesRepository.save(
                ifrFileId = param.ifrFileId,
                remotesRaw = state.remotesRaw,
                pagesLayout = state.pagesLayout
            )
            _state.emit(state.copy(isDownloaded = true))
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
            val remotes: ImmutableList<InfraredRemote>,
            val remotesRaw: String,
            val isDownloaded: Boolean
        ) : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            param: GridScreenDecomposeComponent.Param,
            onIrFileLoaded: (String) -> Unit,
            onUiLoaded: (PagesLayout) -> Unit
        ): GridViewModel
    }
}
