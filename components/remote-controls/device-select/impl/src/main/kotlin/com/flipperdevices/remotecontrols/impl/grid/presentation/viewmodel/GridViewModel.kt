package com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.data.PagesRepository
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class GridViewModel(
    private val pagesRepository: PagesRepository,
    private val param: GridScreenDecomposeComponent.Param
) : DecomposeViewModel() {
    val model = MutableStateFlow<GridComponent.Model>(GridComponent.Model.Loading)

    fun tryLoad() {
        viewModelScope.launch {
            val pagesLayoutResult = pagesRepository.fetchDefaultPageLayout(
                ifrFileId = param.ifrFileId,
            )
            pagesLayoutResult
                .onFailure { model.value = GridComponent.Model.Error }
                .onSuccess { pagesLayout -> model.value = GridComponent.Model.Loaded(pagesLayout) }
        }
    }

    init {
        tryLoad()
    }
}
