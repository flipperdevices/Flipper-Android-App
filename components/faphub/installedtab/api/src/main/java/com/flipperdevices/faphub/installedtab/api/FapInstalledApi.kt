package com.flipperdevices.faphub.installedtab.api

import androidx.compose.runtime.Composable
import com.flipperdevices.faphub.dao.api.model.FapItemShort

interface FapInstalledApi {
    @Composable
    fun getUpdatePendingCount(): Int

    @Composable
    fun ComposableInstalledTab(
        onOpenFapItem: (FapItemShort) -> Unit
    )
}
