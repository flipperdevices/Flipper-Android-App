package com.flipperdevices.faphub.fapscreen.impl.composable.description

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.faphub.dao.api.model.FapItem

@Composable
fun ComposableFapDescription(
    fapItem: FapItem?,
    modifier: Modifier = Modifier
) = Column(modifier) {
    val description = if (fapItem != null) {
        "${fapItem.shortDescription}\n\n${fapItem.description}"
    } else {
        null
    }
    ComposableFapDescriptionText(
        description = description,
    )
    ComposableFapChangelogText(fapItem?.changelog)
    ComposableDeveloperFooter(fapItem?.fapDeveloperInformation)
}
