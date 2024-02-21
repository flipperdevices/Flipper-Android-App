package com.flipperdevices.faphub.maincard.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.installedtab.api.FapUpdatePendingCountApi
import com.flipperdevices.faphub.maincard.impl.model.FapMainCardState
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class FapMainCardViewModel @Inject constructor(
    private val fapNetworkApi: FapNetworkApi,
    targetProviderApi: FlipperTargetProviderApi,
    private val fapUpdatePendingCountApi: FapUpdatePendingCountApi
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "FapMainCardViewModel"

    private val fapMainCardStateFlow = MutableStateFlow<FapMainCardState>(FapMainCardState.Loading)

    private val isExistAppReadyToUpdateStateFlow = MutableStateFlow(false)

    init {
        targetProviderApi.getFlipperTarget().onEach { target ->
            if (target == null) {
                fapMainCardStateFlow.emit(FapMainCardState.Loading)
                return@onEach
            }
            fapNetworkApi.getFeaturedItem(target).onSuccess {
                fapMainCardStateFlow.emit(FapMainCardState.Loaded(it))
            }.onFailure {
                error(it) { "Failed load suggested item" }
                fapMainCardStateFlow.emit(FapMainCardState.FailedLoad)
            }
        }.launchIn(viewModelScope)

        fapUpdatePendingCountApi.getUpdatePendingCount().onEach {
            isExistAppReadyToUpdateStateFlow.emit(it > 0)
        }.launchIn(viewModelScope)
    }

    fun getFapMainCardState() = fapMainCardStateFlow.asStateFlow()

    fun isExistAppReadyToUpdateState() = isExistAppReadyToUpdateStateFlow.asStateFlow()
}
