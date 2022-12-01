package com.flipperdevices.faphub.installedtab.impl.model

import com.flipperdevices.faphub.dao.api.model.FapItem

sealed class FapInstalledScreenState {
    object Loading : FapInstalledScreenState()

    class Loaded(val faps: List<FapItem>) : FapInstalledScreenState()
}
