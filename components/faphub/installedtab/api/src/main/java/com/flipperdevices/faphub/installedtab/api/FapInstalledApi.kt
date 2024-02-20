package com.flipperdevices.faphub.installedtab.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext

@Immutable
interface FapInstalledApi {
    @Composable
    fun getUpdatePendingCount(componentContext: ComponentContext): Int

    @Composable
    @Suppress("NonSkippableComposable")
    fun ComposableInstalledTab(
        componentContext: ComponentContext,
        onOpenFapItem: (uid: String) -> Unit
    )
}
