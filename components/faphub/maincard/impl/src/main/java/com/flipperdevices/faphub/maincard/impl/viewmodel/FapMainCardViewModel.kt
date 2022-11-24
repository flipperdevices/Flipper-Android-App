package com.flipperdevices.faphub.maincard.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.maincard.impl.model.FapMainCardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class FapMainCardViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi
) : ViewModel(), LogTagProvider {
    override val TAG = "FapMainCardViewModel"

    private val fapMainCardStateFlow = MutableStateFlow<FapMainCardState>(FapMainCardState.Loading)

    init {
        viewModelScope.launch {
            runCatching {
                fapNetworkApi.getFeaturedItem()
            }.onSuccess {
                fapMainCardStateFlow.emit(FapMainCardState.Loaded(it))
            }.onFailure {
                error(it) { "Failed load suggested item" }
                fapMainCardStateFlow.emit(FapMainCardState.FailedLoad)
            }
        }
    }

    fun getFapMainCardState(): StateFlow<FapMainCardState> = fapMainCardStateFlow

}