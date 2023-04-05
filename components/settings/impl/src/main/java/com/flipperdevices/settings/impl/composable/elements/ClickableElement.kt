package com.flipperdevices.settings.impl.composable.elements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ClickableElement(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int? = null,
    @StringRes titleId: Int? = null,
    @StringRes descriptionId: Int? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.clickableRipple(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconId != null) {
            Icon(
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                    .size(18.dp),
                painter = painterResource(iconId),
                contentDescription = titleId?.let { stringResource(it) },
                tint = LocalPallet.current.iconTint100
            )
        }
        SimpleElement(
            modifier = Modifier.weight(weight = 1f),
            titleId = titleId,
            descriptionId = descriptionId,
            paddings = if (iconId != null) {
                PaddingValues(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 12.dp)
            } else {
                PaddingValues(12.dp)
            }
        )
        Icon(
            modifier = Modifier
                .size(size = 42.dp)
                .padding(16.dp),
            painter = painterResource(DesignSystem.drawable.ic_navigate),
            tint = LocalPallet.current.iconTint30,
            contentDescription = null
        )
    }
}
