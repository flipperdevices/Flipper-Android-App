package com.flipperdevices.faphub.uninstallbutton.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.appcard.composable.ComposableAppDialogBox
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.uninstallbutton.api.FapUninstallApi
import com.flipperdevices.faphub.uninstallbutton.impl.composable.ComposableFapUninstall
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapUninstallApi::class)
class FapUninstallImpl @Inject constructor() : FapUninstallApi {
    @Composable
    override fun ComposableFapUninstallButton(
        modifier: Modifier,
        applicationUid: String,
        dialogAppBox: @Composable (Modifier) -> Unit
    ) {
        ComposableFapUninstall(
            modifier = modifier,
            applicationUid = applicationUid,
            dialogAppBox = dialogAppBox
        )
    }

    @Composable
    override fun ComposableFapUninstallButton(modifier: Modifier, fapItem: FapItem) {
        ComposableFapUninstallButton(
            modifier = modifier,
            applicationUid = fapItem.id,
            dialogAppBox = {
                ComposableAppDialogBox(
                    fapItem = fapItem,
                    modifier = it
                )
            }
        )
    }

    @Composable
    override fun ComposableFapUninstallButton(modifier: Modifier, fapItem: FapItemShort) {
        ComposableFapUninstallButton(
            modifier = modifier,
            applicationUid = fapItem.id,
            dialogAppBox = {
                ComposableAppDialogBox(
                    fapItem = fapItem,
                    modifier = it
                )
            }
        )
    }
}
