package com.flipperdevices.faphub.appcard.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.appcard.composable.internal.ComposableAppCategory
import com.flipperdevices.faphub.appcard.composable.internal.ComposableAppIcon
import com.flipperdevices.faphub.dao.api.model.FapItem

@Composable
fun SmallHorizontalAppCard(
    modifier: Modifier,
    fapItem: FapItem
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableAppIcon(
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
    modifier: Modifier,
    fapItem: FapItem
) = Column(modifier, verticalArrangement = Arrangement.spacedBy(1.dp)) {
    Text(
        text = fapItem.name,
        style = LocalTypography.current.subtitleM12,
        color = LocalPallet.current.text100
    )
    ComposableAppCategory(category = fapItem.category)
    Text(
        text = fapItem.description,
        style = LocalTypography.current.subtitleR12,
        color = LocalPallet.current.text100,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
