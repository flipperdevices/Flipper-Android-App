package com.flipperdevices.faphub.fapscreen.impl.composable.header

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.faphub.dao.api.model.FapItem

@Composable
fun ComposableFapHeader(
    modifier: Modifier,
    fapItem: FapItem?
) = Column(modifier) {
    ComposableFapTitle(
        modifier = Modifier,
        name = fapItem?.name,
        iconUrl = fapItem?.picUrl,
        fapCategory = fapItem?.category
    )
}