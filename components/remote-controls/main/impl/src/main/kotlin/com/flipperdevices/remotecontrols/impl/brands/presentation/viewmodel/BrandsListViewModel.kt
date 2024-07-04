package com.flipperdevices.remotecontrols.impl.brands.presentation.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.backend.model.BrandModel
import com.flipperdevices.remotecontrols.impl.brands.presentation.data.BrandsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class BrandsListViewModel(
    private val brandsRepository: BrandsRepository,
    private val categoryId: Long
) : DecomposeViewModel() {
    val state = MutableStateFlow<State>(State.Loading)

    fun tryLoad() = viewModelScope.launch {
        state.update { State.Loading }
        brandsRepository.fetchBrands(categoryId)
            .onSuccess { state.value = State.Loaded(it) }
            .onFailure { state.value = State.Error }
            .onFailure(Throwable::printStackTrace)
    }

    init {
        tryLoad()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val brands: List<BrandModel>) : State
        data object Error : State
    }
}
