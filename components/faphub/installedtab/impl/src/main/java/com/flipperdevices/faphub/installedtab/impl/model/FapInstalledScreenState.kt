package com.flipperdevices.faphub.installedtab.impl.model

import com.flipperdevices.faphub.dao.api.model.FapItem
import kotlinx.collections.immutable.ImmutableList

sealed class FapInstalledScreenState {
    object Loading : FapInstalledScreenState()

    class Loaded(
        val faps: ImmutableList<FapItem>
    ) : FapInstalledScreenState()
}
