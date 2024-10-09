package com.flipperdevices.archive.impl.composable.category

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.impl.model.CategoryItem
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableCategoryItem(
    categoryItem: CategoryItem,
    onOpenCategory: (CategoryType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickableRipple { onOpenCategory(categoryItem.categoryType) }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableCategoryIcon(categoryItem.iconId, categoryItem.title)
        Text(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(vertical = 14.dp),
            text = categoryItem.title,
            style = LocalTypography.current.bodyM14
        )
        ComposableCategoryCounter(categoryItem.count)
        Box(
            modifier = Modifier.size(size = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(DesignSystem.drawable.ic_forward),
                contentDescription = null,
                tint = LocalPallet.current.iconTint30
            )
        }
    }
}

@Composable
private fun ComposableCategoryIcon(
    @DrawableRes iconId: Int?,
    description: String
) {
    if (iconId != null) {
        Icon(
            modifier = Modifier
                .padding(end = 8.dp)
                .size(24.dp),
            painter = painterResource(iconId),
            contentDescription = description
        )
    }
}

@Composable
private fun ComposableCategoryCounter(counter: Int?) {
    if (counter == null) {
        CircularProgressIndicator(
            modifier = Modifier.size(size = 18.dp),
            color = LocalPallet.current.progressBarGray
        )
        return
    }

    if (counter != 0) {
        Text(
            modifier = Modifier.padding(horizontal = 2.dp),
            text = counter.toString(),
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.text30
        )
    }
}
