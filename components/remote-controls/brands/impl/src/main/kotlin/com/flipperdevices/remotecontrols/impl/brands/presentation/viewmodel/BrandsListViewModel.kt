package com.flipperdevices.remotecontrols.impl.brands.presentation.viewmodel

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.faphub.errors.api.throwable.FapHubError
import com.flipperdevices.faphub.errors.api.throwable.toFapHubError
import com.flipperdevices.ifrmvp.backend.model.BrandModel
import com.flipperdevices.remotecontrols.impl.brands.presentation.data.BrandsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BrandsListViewModel @AssistedInject constructor(
    private val brandsRepository: BrandsRepository,
    @Assisted private val categoryId: Long
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "BrandsListViewModel"
    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    fun tryLoad() = viewModelScope.launch {
        _state.update { State.Loading }
        brandsRepository.fetchBrands(categoryId)
            .onSuccess { _state.emit(State.Loaded(it.toImmutableList())) }
            .onFailure {
                _state.emit(State.Error(it.toFapHubError()))
            }
            .onFailure { throwable -> error(throwable) { "#tryLoad could not load brands" } }
    }

    init {
        tryLoad()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val brands: ImmutableList<BrandModel>) : State
        data class Error(val throwable: FapHubError) : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            categoryId: Long
        ): BrandsListViewModel
    }
}
