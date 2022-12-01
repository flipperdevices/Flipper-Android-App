package com.flipperdevices.faphub.fapscreen.impl.composable.description

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.faphub.appcard.composable.components.AppCardScreenshots
import com.flipperdevices.faphub.dao.api.model.FapItem

@Composable
fun ComposableFapDescription(
    modifier: Modifier,
    fapItem: FapItem?
) = Column(modifier) {
    AppCardScreenshots(
        modifier = Modifier.padding(top = 12.dp),
        screenshotModifier = Modifier
            .padding(end = 8.dp)
            .size(width = 189.dp, height = 94.dp),
        screenshots = fapItem?.screenshots
    )
    ComposableFapDescriptionText(fapItem?.description)
    ComposableFapChangelogText(fapItem?.changelog)
    ComposableDeveloperFooter(fapItem?.fapDeveloperInformation)
}