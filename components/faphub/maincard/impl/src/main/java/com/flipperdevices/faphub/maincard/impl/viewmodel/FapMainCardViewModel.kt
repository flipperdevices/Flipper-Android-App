package com.flipperdevices.faphub.maincard.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.maincard.impl.model.FapMainCardState
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class FapMainCardViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi,
    private val targetProviderApi: FlipperTargetProviderApi
) : ViewModel(), LogTagProvider {
    override val TAG = "FapMainCardViewModel"

    private val fapMainCardStateFlow = MutableStateFlow<FapMainCardState>(FapMainCardState.Loading)

    init {
        viewModelScope.launch(Dispatchers.Default) {
            targetProviderApi.getFlipperTarget().collectLatest { target ->
                if (target == null) {
                    fapMainCardStateFlow.emit(FapMainCardState.Loading)
                    return@collectLatest
                }
                fapNetworkApi.getFeaturedItem(target).onSuccess {
                    fapMainCardStateFlow.emit(FapMainCardState.Loaded(it))
                }.onFailure {
                    error(it) { "Failed load suggested item" }
                    fapMainCardStateFlow.emit(FapMainCardState.FailedLoad)
                }
            }
        }
    }

    fun getFapMainCardState(): StateFlow<FapMainCardState> = fapMainCardStateFlow
}
