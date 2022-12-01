package com.flipperdevices.faphub.fapscreen.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.fapscreen.impl.api.FAP_ID_KEY
import com.flipperdevices.faphub.fapscreen.impl.model.FapScreenLoadingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class FapScreenViewModel @VMInject constructor(
    @TangleParam(FAP_ID_KEY)
    private val fapId: String,
    private val fapNetworkApi: FapNetworkApi
) : ViewModel() {
    private val fapScreenLoadingStateFlow = MutableStateFlow<FapScreenLoadingState>(
        FapScreenLoadingState.Loading
    )

    init {
        viewModelScope.launch {
            fapNetworkApi.getFapItemById(fapId)
        }
    }

    fun getLoadingState(): StateFlow<FapScreenLoadingState> = fapScreenLoadingStateFlow
}