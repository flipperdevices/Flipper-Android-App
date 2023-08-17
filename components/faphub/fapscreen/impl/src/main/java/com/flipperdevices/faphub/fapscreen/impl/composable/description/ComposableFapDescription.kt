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
    onHideApp: () -> Unit,
    isHidden: Boolean,
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
    ComposableDeveloperFooter(
        fapItem?.fapDeveloperInformation,
        modifier = Modifier.padding(bottom = 26.dp, top = 32.dp)
    )
    var fapReportModifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
    if (fapItem == null) {
        fapReportModifier = fapReportModifier.placeholderConnecting()
    }
    ComposableFapReport(
        modifier = fapReportModifier,
        onClick = onReportApp
    )

    var fapHideModifier = Modifier.padding(vertical = 6.dp)
    if (fapItem == null) {
        fapHideModifier = fapHideModifier.placeholderConnecting()
    }
    ComposableFapHide(
        modifier = fapHideModifier,
        onClick = onHideApp,
        isHidden = isHidden,
        fapItem = fapItem
    )
}
