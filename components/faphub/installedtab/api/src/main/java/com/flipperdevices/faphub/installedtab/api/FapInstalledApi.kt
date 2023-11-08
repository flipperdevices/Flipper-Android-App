package com.flipperdevices.faphub.installedtab.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
interface FapInstalledApi {
    @Composable
    fun getUpdatePendingCount(): Int

    @Composable
    fun ComposableInstalledTab(
        onOpenFapItem: (uid: String) -> Unit
    )
}
