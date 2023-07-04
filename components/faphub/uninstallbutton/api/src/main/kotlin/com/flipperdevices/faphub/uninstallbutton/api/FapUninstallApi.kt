package com.flipperdevices.faphub.uninstallbutton.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.FapItemShort

interface FapUninstallApi {
    @Composable
    fun ComposableFapUninstallButton(
        modifier: Modifier,
        applicationUid: String,
        dialogAppBox: @Composable (Modifier) -> Unit
    )

    @Composable
    fun ComposableFapUninstallButton(
        modifier: Modifier,
        fapItem: FapItem
    )

    @Composable
    fun ComposableFapUninstallButton(
        modifier: Modifier,
        fapItem: FapItemShort
    )
}
