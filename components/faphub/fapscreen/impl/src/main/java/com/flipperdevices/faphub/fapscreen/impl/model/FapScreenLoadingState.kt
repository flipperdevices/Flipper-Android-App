package com.flipperdevices.faphub.fapscreen.impl.model

import com.flipperdevices.faphub.dao.api.model.FapItem

sealed class FapScreenLoadingState {
    object Loading : FapScreenLoadingState()

    data class Loaded(
        val fapItem: FapItem,
        val shareUrl: String,
        val isHidden: Boolean
    ) : FapScreenLoadingState()
    data class Error(
        val throwable: Throwable
    ) : FapScreenLoadingState()
}
