package com.flipperdevices.faphub.catalogtab.impl.composable.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.image.FlipperAsyncImage
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.dao.api.model.FapCategory

@Composable
fun ComposableCategoryCard(
    modifier: Modifier,
    fapCategory: FapCategory?
) {
    val cardModifier = if (fapCategory == null) {
        modifier.placeholderConnecting()
    } else modifier

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ComposableCategoryIcon(
                    modifier = Modifier,
                    category = fapCategory
                )
                Text(
                    text = "0",
                    style = LocalTypography.current.subtitleR10,
                    color = LocalPallet.current.fapHubCategoryText
                )
            }
            Text(
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                text = fapCategory?.name ?: "",
                style = LocalTypography.current.subtitleM12,
                color = LocalPallet.current.text100,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ComposableCategoryIcon(
    modifier: Modifier,
    category: FapCategory?
) {
    var boxModifier = modifier.size(18.dp)
    var isPlaceholderActive by remember { mutableStateOf(true) }
    if (isPlaceholderActive) {
        boxModifier = boxModifier.placeholderConnecting()
    }
    Box(
        modifier = boxModifier
    ) {
        if (category != null) {
            FlipperAsyncImage(modifier = Modifier.fillMaxSize(),
                url = category.picUrl,
                contentDescription = category.name,
                enableMemoryCache = true,
                enableDiskCache = true,
                colorFilter = ColorFilter.tint(LocalPallet.current.text100),
                filterQuality = FilterQuality.None,
                onLoading = { isPlaceholderActive = it })
        }
    }
}