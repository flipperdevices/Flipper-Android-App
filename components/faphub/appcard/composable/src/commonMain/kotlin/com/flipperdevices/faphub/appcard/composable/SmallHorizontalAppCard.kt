package com.flipperdevices.faphub.appcard.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.appcard.composable.components.ComposableAppCategory
import com.flipperdevices.faphub.appcard.composable.components.ComposableAppIcon
import com.flipperdevices.faphub.dao.api.model.FapItemShort

@Composable
fun SmallHorizontalAppCard(
    fapItem: FapItemShort,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableAppIcon(
            modifier = Modifier.size(42.dp),
            url = fapItem.picUrl,
            description = fapItem.name
        )
        SmallHorizontalAppCardText(
            modifier = Modifier.padding(start = 8.dp),
            fapItem = fapItem
        )
    }
}

@Composable
private fun SmallHorizontalAppCardText(
    fapItem: FapItemShort,
    modifier: Modifier = Modifier
) = Column(modifier) {
    Text(
        text = fapItem.name,
        style = LocalTypography.current.subtitleM12,
        color = LocalPallet.current.text100
    )
    ComposableAppCategory(
        category = fapItem.category,
        isSmall = true
    )
    Text(
        text = fapItem.shortDescription,
        style = LocalTypography.current.subtitleR12,
        color = LocalPallet.current.text100,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
