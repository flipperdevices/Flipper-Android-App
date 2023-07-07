package com.flipperdevices.faphub.installedtab.api

import androidx.compose.runtime.Composable

interface FapInstalledApi {
    @Composable
    fun getUpdatePendingCount(): Int

    @Composable
    fun ComposableInstalledTab(
        onOpenFapItem: (uid: String) -> Unit
    )
}
