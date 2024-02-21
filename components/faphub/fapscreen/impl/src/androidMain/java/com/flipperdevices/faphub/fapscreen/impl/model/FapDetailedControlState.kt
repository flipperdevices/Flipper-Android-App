package com.flipperdevices.faphub.fapscreen.impl.model

import com.flipperdevices.faphub.dao.api.model.FapItem

sealed class FapDetailedControlState {
    object Loading : FapDetailedControlState()

    data class Installed(
        val fapItem: FapItem
    ) : FapDetailedControlState()

    data class InProgressOrNotInstalled(
        val fapItem: FapItem
    ) : FapDetailedControlState()
}
