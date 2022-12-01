package com.flipperdevices.faphub.installedtab.api

import androidx.compose.runtime.Composable
import com.flipperdevices.faphub.dao.api.model.FapItem

interface FapInstalledApi {
    @Composable
    fun ComposableInstalledTab(
        onOpenFapItem: (FapItem) -> Unit
    )
}
