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
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<FapUninstallApi>())
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
                queueApiProvider.invoke().enqueue(FapActionRequest.Delete(applicationUid))
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
