package com.flipperdevices.faphub.maincard.impl.model

import com.flipperdevices.faphub.dao.api.model.FapItemShort

sealed class FapMainCardState {
    object Loading : FapMainCardState()

    data class Loaded(val fapItem: FapItemShort) : FapMainCardState()

    object FailedLoad : FapMainCardState()
}
