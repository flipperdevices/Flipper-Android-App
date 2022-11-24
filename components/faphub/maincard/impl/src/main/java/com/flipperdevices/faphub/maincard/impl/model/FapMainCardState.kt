package com.flipperdevices.faphub.maincard.impl.model

import com.flipperdevices.faphub.dao.api.model.FapItem

sealed class FapMainCardState {
    object Loading : FapMainCardState()

    data class Loaded(val fapItem: FapItem) : FapMainCardState()

    object FailedLoad : FapMainCardState()
}