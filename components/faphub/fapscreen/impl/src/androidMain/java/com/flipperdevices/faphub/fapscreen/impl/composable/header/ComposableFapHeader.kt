package com.flipperdevices.faphub.fapscreen.impl.composable.header

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.fapscreen.impl.model.FapDetailedControlState

@Composable
fun ComposableFapHeader(
    fapItem: FapItem?,
    controlState: FapDetailedControlState,
    uninstallButton: @Composable (Modifier) -> Unit,
    onOpenDeviceTab: () -> Unit,
    installationButton: @Composable (FapItem?, Modifier) -> Unit,
    modifier: Modifier = Modifier
) = Column(modifier) {
    ComposableFapTitle(
        modifier = Modifier,
        name = fapItem?.name,
        iconUrl = fapItem?.picUrl,
        fapCategory = fapItem?.category
    )
    ComposableFapMetaInformation(
        modifier = Modifier.padding(vertical = 12.dp),
        metaInformation = fapItem?.metaInformation
    )

    ComposableFapControlRow(
        modifier = Modifier.padding(bottom = 12.dp),
        installationButton = installationButton,
        controlState = controlState,
        uninstallButton = uninstallButton
    )
    if (fapItem != null) {
        ComposableFapBuildStatus(
            modifier = Modifier.padding(bottom = 12.dp),
            fapItem = fapItem,
            onOpenDeviceTab = onOpenDeviceTab
        )
    }
}
