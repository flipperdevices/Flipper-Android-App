package com.flipperdevices.faphub.fapscreen.impl.composable.header

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.faphub.dao.api.model.FapItem

@Composable
fun ComposableFapHeader(
    fapItem: FapItem?,
    modifier: Modifier = Modifier,
    installationButton: @Composable (FapItem?, Modifier, TextUnit) -> Unit
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
        fapItem = fapItem,
        installationButton = installationButton
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableFapHeaderPreview() {
    FlipperThemeInternal {
        ComposableFapHeader(
            fapItem = null,
            Modifier.padding(horizontal = 24.dp),
        ) { _, _, _ -> }
    }
}
