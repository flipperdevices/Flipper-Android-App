package com.flipperdevices.faphub.fapscreen.impl.composable.description

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.faphub.dao.api.model.FapItem

@Composable
fun ComposableFapDescription(
    modifier: Modifier,
    fapItem: FapItem?
) = Column(modifier) {
    ComposableFapDescriptionText(fapItem?.description)
    ComposableFapChangelogText(fapItem?.changelog)
    ComposableDeveloperFooter(fapItem?.fapDeveloperInformation)
}