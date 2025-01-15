package com.flipperdevices.faphub.appcard.composable.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.image.FlipperAsyncImage
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.dao.api.model.FapCategory

private const val DEFAULT_CATEGORY_NAME = "Loading"

@Composable
fun ComposableAppCategory(
    category: FapCategory?,
    modifier: Modifier = Modifier,
    isSmall: Boolean = false
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    ComposableAppCategoryIcon(
        category = category,
        size = if (isSmall) {
            12.dp
        } else {
            14.dp
        }
    )
    var textModifier = Modifier
        .padding(start = 4.dp)

    if (category == null) {
        textModifier = textModifier.placeholderConnecting()
    }
    val baseStyle = if (isSmall) {
        LocalTypography.current.subtitleR12
    } else {
        LocalTypography.current.bodyR14
    }

    Text(
        modifier = textModifier,
        text = category?.name ?: DEFAULT_CATEGORY_NAME,
        style = baseStyle.copy(
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.Both
            )
        ),
        color = LocalPallet.current.text60
    )
}

@Composable
private fun ComposableAppCategoryIcon(
    category: FapCategory?,
    size: Dp
) {
    val modifierWithClip = Modifier.size(size)
    var isPlaceholderActive by remember { mutableStateOf(true) }
    val modifierWithPlaceholder = if (isPlaceholderActive) {
        modifierWithClip.placeholderConnecting()
    } else {
        modifierWithClip
    }
    Box(
        modifier = modifierWithPlaceholder
    ) {
        if (category != null) {
            FlipperAsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                url = category.picUrl,
                contentDescription = category.name,
                colorFilter = ColorFilter.tint(LocalPallet.current.text60),
                filterQuality = FilterQuality.None,
                onLoading = { isPlaceholderActive = it },
                cacheKey = category.name
            )
        }
    }
}
