package com.flipperdevices.faphub.appcard.composable.internal

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.image.FlipperAsyncImageWithPlaceholder
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
internal fun ComposableAppCategory(
    modifier: Modifier = Modifier,
    category: com.flipperdevices.faphub.dao.api.model.FapCategory
) = Row(modifier) {
    FlipperAsyncImageWithPlaceholder(
        modifier = Modifier.size(12.dp),
        url = category.picUrl,
        contentDescription = category.name,
        enableMemoryCache = true,
        enableDiskCache = true,
        colorFilter = ColorFilter.tint(LocalPallet.current.text60),
        filterQuality = FilterQuality.None
    )
    Text(
        modifier = Modifier.padding(start = 4.dp),
        text = category.name,
        style = LocalTypography.current.subtitleR10,
        color = LocalPallet.current.text60
    )
}
