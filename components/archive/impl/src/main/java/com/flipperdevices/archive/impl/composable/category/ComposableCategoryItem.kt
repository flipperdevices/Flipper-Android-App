package com.flipperdevices.archive.impl.composable.category

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.archive.impl.model.CategoryItem
import com.flipperdevices.core.ui.R as DesignSystem

@Composable
fun ComposableCategoryItem(
    categoryItem: CategoryItem,
    onPress: (CategoryItem) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = { onPress(categoryItem) }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableCategoryIcon(categoryItem.iconId, categoryItem.title)
        Text(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(vertical = 14.dp),
            text = categoryItem.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            color = colorResource(DesignSystem.color.black_100)
        )
        ComposableCategoryCounter(categoryItem.count)
        Box(
            modifier = Modifier.size(size = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(DesignSystem.drawable.ic_forward),
                contentDescription = null,
                tint = colorResource(DesignSystem.color.black_30)
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
            modifier = Modifier.size(24.dp),
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
            color = colorResource(DesignSystem.color.black_30)
        )
        return
    }

    if (counter != 0) {
        Text(
            modifier = Modifier.padding(horizontal = 2.dp),
            text = counter.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            color = colorResource(DesignSystem.color.black_30)
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
@Suppress("UnusedPrivateMember")
private fun ComposableCategoryItemPreview() {
    ComposableCategoryItem(CategoryItem(null, "Test Protocol", count = null)) {}
}
