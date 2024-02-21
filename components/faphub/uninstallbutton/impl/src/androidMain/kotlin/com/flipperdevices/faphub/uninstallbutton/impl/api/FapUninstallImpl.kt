package com.flipperdevices.faphub.uninstallbutton.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.appcard.composable.ComposableAppDialogBox
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.uninstallbutton.api.FapUninstallApi
import com.flipperdevices.faphub.uninstallbutton.impl.composable.ComposableFapUninstall
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class, FapUninstallApi::class)
class FapUninstallImpl @Inject constructor(
    private val queueApiProvider: Provider<FapInstallationQueueApi>
) : FapUninstallApi {
    @Composable
    override fun ComposableFapUninstallButton(
        modifier: Modifier,
        applicationUid: String,
        dialogAppBox: @Composable (Modifier) -> Unit
    ) {
        ComposableFapUninstall(
            modifier = modifier,
            dialogAppBox = dialogAppBox,
            onDelete = {
                queueApiProvider.get().enqueue(FapActionRequest.Delete(applicationUid))
            }
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
