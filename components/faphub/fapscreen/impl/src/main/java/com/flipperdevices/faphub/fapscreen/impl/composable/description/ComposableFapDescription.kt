package com.flipperdevices.faphub.fapscreen.impl.composable.description

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.faphub.dao.api.model.FapItem

@Composable
fun ComposableFapDescription(
    fapItem: FapItem?,
    onReportApp: () -> Unit,
    modifier: Modifier = Modifier
) = Column(modifier) {
    val description = if (fapItem != null) {
        if (fapItem.description.startsWith(fapItem.shortDescription)) {
            fapItem.description
        } else {
            "${fapItem.shortDescription}\n\n${fapItem.description}"
        }
    } else {
        null
    }
    ComposableFapDescriptionText(
        description = description,
    )
    ComposableFapChangelogText(fapItem?.changelog)
    ComposableDeveloperFooter(fapItem?.fapDeveloperInformation)
    var fapReportManifest = Modifier.padding(top = 32.dp)
    if (fapItem == null) {
        fapReportManifest = fapReportManifest.placeholderConnecting()
    }
    ComposableFapReport(
        modifier = fapReportManifest,
        onClick = onReportApp
    )
}
