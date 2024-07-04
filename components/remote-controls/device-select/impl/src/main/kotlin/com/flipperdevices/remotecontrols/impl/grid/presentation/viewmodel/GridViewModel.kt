package com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.data.PagesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class GridViewModel(
    private val pagesRepository: PagesRepository,
    private val param: GridScreenDecomposeComponent.Param,
    private val onIrFileLoaded: (String) -> Unit
) : DecomposeViewModel() {
    val state = MutableStateFlow<State>(State.Loading)

    private suspend fun loadIrFileContent(): Boolean {
        return pagesRepository.fetchKeyContent(param.ifrFileId)
            .onFailure { state.value = State.Error }
            .onSuccess(onIrFileLoaded)
            .getOrNull() != null
    }

    fun tryLoad() {
        viewModelScope.launch {
            val isIrFileLoaded = loadIrFileContent()
            if (!isIrFileLoaded) return@launch
            val pagesLayoutResult = pagesRepository.fetchDefaultPageLayout(
                ifrFileId = param.ifrFileId,
            )
            pagesLayoutResult
                .onFailure { state.value = State.Error }
                .onSuccess { pagesLayout -> state.value = State.Loaded(pagesLayout) }
        }
    }

    init {
        tryLoad()
    }

    sealed interface State {
        data object Loading : State
        data object Error : State
        data class Loaded(val pagesLayout: PagesLayout) : State
    }
}
